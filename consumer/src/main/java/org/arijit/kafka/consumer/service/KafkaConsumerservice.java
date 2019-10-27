package org.arijit.kafka.consumer.service;

import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.consumer.config.KafkaConsumerConfig;
import org.arijit.kafka.consumer.context.KafkaConsumerContext;
import org.arijit.kafka.consumer.job.KafkaConsumerJob;
import org.arijit.kafka.consumer.message.processor.IMessageProcessor;
import org.arijit.kafka.consumer.message.processor.SimpleMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KafkaConsumerservice will initialize consumer connection with kafka.
 * Also it will expose start method to start consuming.
 * @author arijit
 *
 */
@Component
public class KafkaConsumerservice {

	private static Logger logger = LogManager.getLogger(KafkaConsumerservice.class);
	@Autowired
	public KafkaConsumerContext kafkaConsumerContext;

	private ScheduledThreadPoolExecutor consumerExecutor;

	private Properties consumerProperties;
	private KafkaConsumerConfig kafkaConsumerConfig;

	public void init() {
		kafkaConsumerConfig = kafkaConsumerContext.getKafkaConsumerConfig();
		logger.info("Kafka Consumer Config: {}", kafkaConsumerConfig);
		createConsumerProperties();
//		consumerExecutor = Executors.newScheduledThreadPool(kafkaConsumerConfig.getConsumerThreadCount());
		consumerExecutor = new ScheduledThreadPoolExecutor(kafkaConsumerConfig.getConsumerThreadCount());
	}

	private void createConsumerProperties() {
		consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerConfig.getBootStrapervers());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerConfig.getGroupId());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfig.getKeyDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				kafkaConsumerConfig.getValueDeserializer());
		consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerConfig.getSessionTimeOut());
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerConfig.getAutoOffsetReset());

		if (kafkaConsumerConfig.isEnableAutoCommit()) {
			logger.warn("Kafka Autocommit is enabled. Make sure you have done this intentinally");
			consumerProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
					kafkaConsumerConfig.getAutoCommitInterval());
			consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		}
	}

	/**
	 * Start method will start consuming from specified kafka topic.
	 */
	public void start(String topicName) {
		try {
			IMessageProcessor<Integer, String> messageProcessor = new SimpleMessageProcessor();
			logger.info("Starting consuming from: " + topicName);
			for (int i = 0; i < kafkaConsumerConfig.getConsumerThreadCount(); i++) {
				KafkaConsumerJob<Integer, String> consumerJob = new KafkaConsumerJob<Integer, String>(topicName,
						consumerProperties, messageProcessor);
				consumerExecutor.scheduleAtFixedRate(consumerJob, 10, 1000, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			logger.error("Unable to start Kafka Consumer", e);
		}
	}

	public void stop() {
		consumerExecutor.shutdown();
	}

}
