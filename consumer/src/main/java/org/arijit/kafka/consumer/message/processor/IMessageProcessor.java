package org.arijit.kafka.consumer.message.processor;

import java.util.List;

import org.arijit.kafka.consumer.mesage.dto.KafkaConsumedMessageDto;

public interface IMessageProcessor<K,V> {

	public void process(List<KafkaConsumedMessageDto<K, V>> consumedList);
}
