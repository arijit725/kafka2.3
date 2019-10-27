package org.arijit.kafka.consumer.context;

import org.arijit.kafka.consumer.config.KafkaConsumerConfig;
import org.arijit.kafka.consumer.service.KafkaConsumerservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("org.arijit.kafka.consumer")
public class KafkaConsumerContext {

	@Autowired
	private KafkaConsumerConfig kafkaConsumerConfig;
	
	@Bean("kafkaConsumerService")
	@Scope("singleton")
	public KafkaConsumerservice getKafkaConsumerService() {
		return new KafkaConsumerservice();
	}
	
	public KafkaConsumerConfig getKafkaConsumerConfig() {
		return kafkaConsumerConfig;
	}
}
