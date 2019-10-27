package org.arijit.kafka.consumer.job;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.consumer.mesage.dto.KafkaConsumedMessageDto;
import org.arijit.kafka.consumer.message.processor.IMessageProcessor;

/**
 * KafkaConsumerJob will consume message from a particular topic.
 * 
 * @author arijit
 *
 * @param <K>
 * @param <V>
 */
public class KafkaConsumerJob<K, V> implements Runnable {

	private static Logger logger = LogManager.getLogger(KafkaConsumerJob.class);
	private final String topicName;
	private Consumer<K, V> consumer;
	private final IMessageProcessor<K, V> messageProcessor;
	/*
	 * Maximum time one thread are allowed not to consume any recrods before they
	 * show warning
	 */
	private int skipCount = 10;

	public KafkaConsumerJob(String topicName, Properties consumerProperties, IMessageProcessor<K, V> messageProcessor) {
		this.topicName = topicName;
		this.messageProcessor = messageProcessor;
		try {
			this.consumer = new KafkaConsumer<>(consumerProperties);
			consumer.subscribe(Collections.singletonList(topicName));
			logger.info("Kafka Consumer Intitated by: " + Thread.currentThread().getName());
		} catch (Exception e) {
			logger.error("Unable to create consumer: ", e);
		}

	}

	@Override
	public void run() {
		try {
			if (Thread.interrupted()) {
				consumer.close();
				logger.info("Consumer Closed by: " + Thread.currentThread().getName());
				return;
			}
			if(logger.isDebugEnabled())
				logger.info("Running : " + Thread.currentThread().getName());
			Duration duration = Duration.ofMillis(1000);
			ConsumerRecords<K, V> consumerRecords = consumer.poll(duration);

			if (consumerRecords.count() == 0) {
				skipCount--;
				if (skipCount == 0) {
					logger.warn("No records consumed from kafka topic: " + topicName);
					skipCount = 10;
				}
				return;
			}
			List<KafkaConsumedMessageDto<K, V>> consumedList = new ArrayList<>(consumerRecords.count());
			consumerRecords.forEach(record -> {
				if (logger.isDebugEnabled()) {
					logger.info(
							"Consumer Record: Record Key: {}, Record Value: {}, Record Partition: {}, Record offser: {})",
							record.key(), record.value(), record.partition(), record.offset());
				}
				KafkaConsumedMessageDto<K, V> kafkaConsumedMessageDto = KafkaConsumedMessageDto
						.createMessage(record.key(), record.value(), topicName, record.partition(), record.offset());
				consumedList.add(kafkaConsumedMessageDto);
			});
			messageProcessor.process(consumedList);
			consumer.commitAsync();
			skipCount = 10;
		} catch (Throwable e) {
			logger.error("Unable to consume: ", e);
		}
	}

}
