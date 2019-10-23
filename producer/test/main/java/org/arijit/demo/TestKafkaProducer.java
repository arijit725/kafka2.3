package org.arijit.demo;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.dto.KafkaMessageDto;
import org.arijit.kafka.producer.KafkaProducerWrapper;
import org.arijit.kafka.producer.config.KafkaProducerContext;
import org.arijit.kafka.producer.service.KafkaProducerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class TestKafkaProducer {

	private static final Logger logger = LogManager.getLogger(TestKafkaProducer.class);
	

   
	
	public void sendMessage() {
		KafkaProducerService kafkaProducerService = new KafkaProducerService();
		kafkaProducerService.init();
	}
	public static void main(String args[]) throws InterruptedException, ExecutionException {
		logger.info("Starting TestKafkaProducer");
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(KafkaProducerContext.class);
		KafkaProducerService kafkaProducerService = context.getBean("kafkaProducerService",KafkaProducerService.class);
		logger.info("kafkaProducerService instance 1: "+kafkaProducerService);
		kafkaProducerService = context.getBean("kafkaProducerService",KafkaProducerService.class);
		logger.info("kafkaProducerService instance 2: "+kafkaProducerService);			
		kafkaProducerService.init();
		KafkaProducerWrapper<Integer, String> producer = kafkaProducerService.createProducer("topic-1");
		int i=0;
		while(i<10000) {				
			KafkaMessageDto<Integer, String> message = KafkaMessageDto.createMessage(i, "This is a test: "+i);
			producer.sendSync(message);
			i++;
		}			
		context.registerShutdownHook();
		context.close();		
	}
}
