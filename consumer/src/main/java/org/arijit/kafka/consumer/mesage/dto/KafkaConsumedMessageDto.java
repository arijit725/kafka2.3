package org.arijit.kafka.consumer.mesage.dto;

/**
 * All messages consumed from Kafka will be converted to
 * KafkaConsumedMessageDto. This will be used for further processing. If any
 * internal changes happens in KafkaConsumer, all we need to make changes into
 * KafkaConsumerJob. This way out code will be unimpacted.
 * 
 * @author arijit
 *
 * @param <K>
 * @param <V>
 */
public final class KafkaConsumedMessageDto<K, V> {

	private final K key;
	private final V value;
	private final String topicName;
	private final int partition;
	private final long offSet;

	private KafkaConsumedMessageDto(K key, V value, String topicName, int partition, long offset) {
		this.key = key;
		this.value = value;
		this.topicName = topicName;
		this.partition = partition;
		this.offSet = offset;
	}

	public static <K, V> KafkaConsumedMessageDto<K, V> createMessage(K key, V value, String topicName, int partition,
			long offset) {
		return new KafkaConsumedMessageDto<K, V>(key, value, topicName, partition, offset);
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public int getPartition() {
		return partition;
	}

	public long getOffSet() {
		return offSet;
	}

	public String getTopicName() {
		return topicName;
	}

	@Override
	public String toString() {
		return "KafkaConsumedMessageDto [key=" + key + ", value=" + value + ", topicName=" + topicName + ", partition="
				+ partition + ", offSet=" + offSet + "]";
	}

}
