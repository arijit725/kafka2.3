package org.arijit.kafka.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.dto.KafkaMessageDto;
import org.arijit.kafka.producer.service.KafkaProducerService;

public class KafkaProducerWrapper<K, V> {

	private static final Logger logger = LogManager.getLogger(KafkaProducerService.class);

	private final String topicName;
	private final KafkaProducer<K, V> kafkaProducer;

	public KafkaProducerWrapper(String topicName, Properties kafkaProperties) {
		this.topicName = topicName;
		this.kafkaProducer = new KafkaProducer<>(kafkaProperties);
	}

	/**
	 * Send message in sync to topic
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public RecordMetadata sendSync(KafkaMessageDto<K, V> message) throws InterruptedException, ExecutionException {
		if(logger.isDebugEnabled())
			logger.debug("Sending Message: "+message);
		ProducerRecord<K, V> record = null;
		if (message.getPartition() == null)
			record = new ProducerRecord<K, V>(topicName, message.getKey(), message.getValue());
		else
			record = new ProducerRecord<K, V>(topicName, message.getPartition(), message.getTimeStamp(),
					message.getKey(), message.getValue());
		RecordMetadata recordMetadata = this.kafkaProducer.send(record).get();
		if(logger.isDebugEnabled()) {
			logger.debug("Message Sent: Partiton- {} Offset- {}",recordMetadata.partition(),recordMetadata.offset());
		}
		return recordMetadata;
	}

	public void sendAsync() {

	}
}
