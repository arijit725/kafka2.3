package org.arijit.kafka.consumer.message.processor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.consumer.job.KafkaConsumerJob;
import org.arijit.kafka.consumer.mesage.dto.KafkaConsumedMessageDto;

/**
 * Simple Message Processor will accept the messages consumed by kafka consumer.
 * At this point what type of messages we are going to process. So we can
 * typecast them at class Level itself. and print it
 * 
 * @author arijit
 *
 */
public class SimpleMessageProcessor implements IMessageProcessor<Integer, String> {

	private static Logger logger = LogManager.getLogger(KafkaConsumerJob.class);

	@Override
	public void process(List<KafkaConsumedMessageDto<Integer, String>> consumedList) {
		if(logger.isDebugEnabled())
			logger.debug("Inside SimpleMessageProcessor:: " + this);
		consumedList.forEach(message -> {
			logger.info("Consumed Message: {} [ topicName: {} Partiton: {} Offset: {}]", message.getValue(),
					message.getTopicName(), message.getPartition(), message.getOffSet());
		});
	}

}
