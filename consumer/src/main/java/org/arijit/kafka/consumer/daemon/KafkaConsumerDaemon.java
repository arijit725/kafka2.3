package org.arijit.kafka.consumer.daemon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.consumer.context.KafkaConsumerContext;
import org.arijit.kafka.consumer.service.KafkaConsumerservice;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class KafkaConsumerDaemon {

	private static Logger logger = LogManager.getLogger(KafkaConsumerDaemon.class);
	public static void main(String args[]) {
		AbstractApplicationContext applicationContext = new AnnotationConfigApplicationContext(KafkaConsumerContext.class);
		KafkaConsumerservice kafkaConsumerService =  applicationContext.getBean("kafkaConsumerService", KafkaConsumerservice.class);
		logger.info("Instantiated kafkaConsumerService: "+kafkaConsumerService);	
		kafkaConsumerService.init();
		kafkaConsumerService.start("testtopic");
		applicationContext.registerShutdownHook();
		applicationContext.close();
	}
}
