package org.arijit.kafka.producer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * KafkaProducerConfig will hold properties of kafka required to create
 * producer.
 * 
 * @author arijit
 *
 */
@Configuration
public class KafkaProducerConfig {

	/**
	 * bootstrap.servers are the broker details of kafka. This could be defined as
	 * ',' seperated string ex: localhost:9092, localhost:9093
	 */
	@Value("${kafka.producer.bootstrap.server:localhost:9091,localhost:9092}")
	private String bootStrapServers;
	/**
	 * client.id is the unique identifier for each producer.
	 */
	@Value("${kafka.producer.client.id:testClient1}")
	private String clientId;
	@Value("${kafka.producer.key.serializer:org.apache.kafka.common.serialization.IntegerSerializer}")
	private String keySerializer;
	@Value("${kafka.producer.value.serializer:org.apache.kafka.common.serialization.StringSerializer}")
	private String valueSerializer;
	@Value("${kafka.producer.ack.config:all}")
	private String ack;
	/**
	 * wait upto linger in ms before sending batch if size not met
	 */
	@Value("${kafka.producer.linger.ms:100}")
	private int lingerMs;

	@Value("${kafka.producer.batch.size:65536}")
	private int batchSize;
	@Value("${kafka.producer.retry.count:1}")
	private int retries;
	
//	RETRY_BACKOFF_MS_CONFIG
	/**
	 * 
	 */
	@Value("${kafka.producer.requst.timeout.ms:15000}")
	private int requestTimeOutMS;

	
	public String getBootStrapServers() {
		return bootStrapServers;
	}


	public String getClientId() {
		return clientId;
	}


	public String getKeySerializer() {
		return keySerializer;
	}


	public String getValueSerializer() {
		return valueSerializer;
	}


	public String getAck() {
		return ack;
	}


	public int getLingerMs() {
		return lingerMs;
	}


	public int getBatchSize() {
		return batchSize;
	}


	public int getRetries() {
		return retries;
	}


	public int getRequestTimeOutMS() {
		return requestTimeOutMS;
	}


	@Override
	public String toString() {
		return "KafkaProducerConfig [bootStrapServers=" + bootStrapServers + ", clientId=" + clientId
				+ ", keySerializer=" + keySerializer + ", valueSerializer=" + valueSerializer + ", ack=" + ack
				+ ", lingerMs=" + lingerMs + ", batchSize=" + batchSize + ", retries=" + retries + ", requestTimeOutMS="
				+ requestTimeOutMS + "]";
	}
	
	

}
