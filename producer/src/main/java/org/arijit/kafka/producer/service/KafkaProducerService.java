package org.arijit.kafka.producer.service;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.producer.KafkaProducerWrapper;
import org.arijit.kafka.producer.config.KafkaProducerConfig;
import org.arijit.kafka.producer.config.KafkaProducerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * KafkaProducerService will initialize the proucer service for kafka
 * This producer service will return producer object per topic basis.
 * To write into a certain topic we will use the returned topic.
 * @author arijit
 *
 */

public class KafkaProducerService {

	private static final Logger logger = LogManager.getLogger(KafkaProducerService.class);
	
	@Autowired
	private KafkaProducerContext kafkaProducerContext;
	
	private static Properties properties;
	public void init() {
		KafkaProducerConfig kafkaProducerConfig = kafkaProducerContext.getKafkaProducerConfig();
		logger.info("KafkaProducerConfig Details: "+kafkaProducerConfig);
		properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerConfig.getBootStrapServers());
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerConfig.getClientId());
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getKeySerializer());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getValueSerializer());
		properties.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfig.getAck());
		properties.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfig.getLingerMs());
		properties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfig.getBatchSize());
		properties.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfig.getRetries());
		logger.info("Kafka Properties: "+properties);		
	}
	
	
	
	public <K extends Object,V extends Object>  KafkaProducerWrapper<K,V> createProducer(String topicName) {
		return new KafkaProducerWrapper<K,V>(topicName, properties);        
	}
}
