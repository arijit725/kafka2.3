package org.arijit.kafka.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfig {
	
	@Value("${kafka.consumer.bootstrap.server:localhost:9091,localhost:9092}")
	private String bootStrapervers;
	@Value("${kafka.consumer.group.id:test-consumer-group}")
	private String groupId;
	@Value("${kafka.consumer.key.deserializer:org.apache.kafka.common.serialization.IntegerDeserializer}")
	private String keyDeserializer;
	@Value("${kafka.consumer.value.deserializer:org.apache.kafka.common.serialization.StringDeserializer}")
	private String valueDeserializer;
	@Value("${kafka.consumer.session.timeout:10000}")
	private int sessionTimeOut;
	@Value("${kafka.consumer.auto.offset.reset:earliest}")
//	String must be one of: latest, earliest, none
	private String autoOffsetReset;
	@Value("${kafka.consumer.enable.autocommit:false}")
	private boolean enableAutoCommit;
	@Value("${kafka.consumer.autocommit.interval:10000}")
	private long autoCommitInterval;	
	@Value("${kafka.consumer.thread.count:5}")
	private int consumerThreadCount;
	public String getBootStrapervers() {
		return bootStrapervers;
	}
	public String getGroupId() {
		return groupId;
	}
	public String getKeyDeserializer() {
		return keyDeserializer;
	}
	public String getValueDeserializer() {
		return valueDeserializer;
	}
	public int getSessionTimeOut() {
		return sessionTimeOut;
	}
	public String getAutoOffsetReset() {
		return autoOffsetReset;
	}
	public boolean isEnableAutoCommit() {
		return enableAutoCommit;
	}
	public long getAutoCommitInterval() {
		return autoCommitInterval;
	}
	public int getConsumerThreadCount() {
		return consumerThreadCount;
	}
	@Override
	public String toString() {
		return "KafkaConsumerConfig [bootStrapervers=" + bootStrapervers + ", groupId=" + groupId + ", keyDeserializer="
				+ keyDeserializer + ", valueDeserializer=" + valueDeserializer + ", sessionTimeOut=" + sessionTimeOut
				+ ", autoOffsetReset=" + autoOffsetReset + ", enableAutoCommit=" + enableAutoCommit
				+ ", autoCommitInterval=" + autoCommitInterval + ", consumerThreadCount=" + consumerThreadCount + "]";
	}
	
	
	
}
