package org.arijit.kafka.producer.config;

import org.arijit.kafka.producer.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
/**
 * KafkaProducerContext will hold all Beans and config object reference.
 * This context will be responsible to programatically create Spring Beans.
 * Also this will hold references of autowired beans.
 * 
 * @author arijit
 *
 */
@Configuration
@ComponentScan("org.arijit")
public class KafkaProducerContext {
	
	@Autowired
	private KafkaProducerConfig kafkaProducerConfig;
	
	/**
	 * getKafkaProducerService() method will create singleton instance of KafkaProducerService.
	 * Here we are progaramtically configuring KafkaProducerService as a bean named as kafkaProducerService.
	 * @return
	 */
	@Bean("kafkaProducerService")
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public KafkaProducerService getKafkaProducerService() {
		return new KafkaProducerService();
	}
	
	
	
	public KafkaProducerConfig getKafkaProducerConfig() {
		return kafkaProducerConfig;
	}
}
