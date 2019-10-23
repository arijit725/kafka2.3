package org.arijit.kafka.dto;

public final class KafkaMessageDto<K extends Object,V extends Object> {

	private final K key;
	private final V value;
	private final Long timeStamp;
	private final Integer partition;
	
	private KafkaMessageDto(K key, V value, long timeStamp, int partition ) {
		this.key = key;
		this.value = value;
		this.timeStamp = timeStamp;
		this.partition = partition;				
	}
	
	private KafkaMessageDto(K key, V value) {
		this.key = key;
		this.value = value;
		this.timeStamp = null;
		this.partition= null;
	}
	
	public static <K,V> KafkaMessageDto<K, V> createMessage(K key, V value, long timeStamp, int partition){
		return new KafkaMessageDto<K, V>(key, value, timeStamp, partition);
	}
	
	public static <K,V> KafkaMessageDto<K, V> createMessage(K key, V value){
		return new KafkaMessageDto<K, V>(key, value);
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public Integer getPartition() {
		return partition;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}

	@Override
	public String toString() {
		return "KafkaMessageDto [key=" + key + ", value=" + value + ", timeStamp=" + timeStamp + ", partition="
				+ partition + "]";
	}
	
	
	
	
}
