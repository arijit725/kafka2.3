package org.arijit.kafka.daemon;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.dto.KafkaMessageDto;
import org.arijit.kafka.producer.KafkaProducerWrapper;
import org.arijit.kafka.producer.config.KafkaProducerContext;
import org.arijit.kafka.producer.service.KafkaProducerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Kafka Producer Daemon will produce messages into kafka. Here I have generated
 * messages and pushed, but messages could be consumed from anywhere
 * 
 * @author arijit
 *
 */
public class KafkProducerDaemon {

	private static final Logger logger = LogManager.getLogger(KafkProducerDaemon.class);
	
	public static void main(String args[]) throws InterruptedException, ExecutionException {
		logger.info("Starting TestKafkaProducer");
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(KafkaProducerContext.class);
		KafkaProducerService kafkaProducerService = context.getBean("kafkaProducerService", KafkaProducerService.class);
		logger.info("kafkaProducerService instance 1: " + kafkaProducerService);
		kafkaProducerService = context.getBean("kafkaProducerService", KafkaProducerService.class);
		logger.info("kafkaProducerService instance 2: " + kafkaProducerService);
		kafkaProducerService.init();
		KafkaProducerWrapper<Integer, String> producer = kafkaProducerService.createProducer("testtopic");
		int i = 0;
		while (i < 10000) {
			KafkaMessageDto<Integer, String> message = KafkaMessageDto.createMessage(i, "This is a test: " + i);
			producer.sendSync(message);
			i++;
		}
		context.registerShutdownHook();
		context.close();
	}
}
