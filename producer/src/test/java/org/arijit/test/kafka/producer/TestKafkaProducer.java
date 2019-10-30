package org.arijit.test.kafka.producer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.Time;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.dto.KafkaMessageDto;
import org.arijit.kafka.producer.KafkaProducerWrapper;
import org.arijit.kafka.producer.config.KafkaProducerConfig;
import org.arijit.kafka.producer.config.KafkaProducerContext;
import org.arijit.kafka.producer.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;

import kafka.utils.MockTime;
import kafka.utils.TestUtils;
import kafka.zk.EmbeddedZookeeper;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

@Test
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class TestKafkaProducer extends AbstractTestNGSpringContextTests {

	private static final Logger logger = LogManager.getLogger(TestKafkaProducer.class);

	private AbstractApplicationContext applicationContext;
	private String topicName = "testTopic";
	EmbeddedKafkaRule embeddedKafka;
	KafkaServer kafkaServer;

	@BeforeClass
	public void setup() {
		applicationContext = new AnnotationConfigApplicationContext(KafkaProducerContext.class);
//		setupEmbededKafka();
		try {
			startkafkaServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startkafkaServer() throws IOException {
		EmbeddedZookeeper zkServer = new EmbeddedZookeeper();
		String zkConnect = "localhost:" + zkServer.port();
		ZkClient zkClient = new ZkClient(zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
		ZkUtils zkUtils = ZkUtils.apply(zkClient, false);

//	    File file = new File("testKafkaDir")
		// setup Broker
		String BROKERHOST = "localhost";
		String BROKERPORT = "9091";
		Properties brokerProps = new Properties();
		brokerProps.setProperty("zookeeper.connect", zkConnect);
		brokerProps.setProperty("broker.id", "0");
		brokerProps.setProperty("log.dirs", Files.createTempDirectory("kafkaUtils-").toAbsolutePath().toString());
		brokerProps.setProperty("listeners", "PLAINTEXT://" + BROKERHOST + ":" + BROKERPORT);
		KafkaConfig config = new KafkaConfig(brokerProps);
		Time mock = new MockTime();
		kafkaServer = TestUtils.createServer(config, mock);
		kafkaServer.startup();

		// create topics
		AdminUtils.createTopic(zkUtils, topicName, 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);
	    
	}

	@Test
	public void test() {
		logger.info("This is a test");

//		Map<String, Object> senderProperties =
//		        KafkaTestUtils.senderProps(
//		            embeddedKafka.getEmbeddedKafka().getBrokersAsString());
//		logger.info("Embeded Kafka senderProperties: "+senderProperties);
		KafkaProducerService kafkaProducerService = applicationContext.getBean("kafkaProducerService",
				KafkaProducerService.class);
		kafkaProducerService.init();
		KafkaProducerWrapper<Integer, String> producer = kafkaProducerService.createProducer(topicName);
		String message = "this is a test-";
		for (int i = 0; i < 10; i++) {
			KafkaMessageDto<Integer, String> messageDto = KafkaMessageDto.createMessage(i, message + i);
			try {
				producer.sendSync(messageDto);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Consumer<Long, String> consumer = createConsumer();
		final int giveUp = 100;
		int noRecordsCount = 0;
		while (true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);

			if (consumerRecords.count() == 0) {
				noRecordsCount++;
				if (noRecordsCount > giveUp)
					break;
				else
					continue;
			}

			consumerRecords.forEach(record -> {
				System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(),
						record.partition(), record.offset());
			});

			consumer.commitAsync();
		}
	}

	private Consumer<Long, String> createConsumer() {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		// Create the consumer using props.
		final Consumer<Long, String> consumer = new KafkaConsumer<>(props);

		// Subscribe to the topic.
		consumer.subscribe(Collections.singletonList(topicName));
		return consumer;
	}

	@AfterClass
	public void destroy() {
		kafkaServer.shutdown();
	}
}
