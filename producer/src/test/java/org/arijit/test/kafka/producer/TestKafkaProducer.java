package org.arijit.test.kafka.producer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.Time;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arijit.kafka.dto.KafkaMessageDto;
import org.arijit.kafka.producer.KafkaProducerWrapper;
import org.arijit.kafka.producer.config.KafkaProducerContext;
import org.arijit.kafka.producer.service.KafkaProducerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.MockTime;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;

@Test
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class TestKafkaProducer extends AbstractTestNGSpringContextTests {

	private static final Logger logger = LogManager.getLogger(TestKafkaProducer.class);

	private AbstractApplicationContext applicationContext;
	private String topicName = "testTopic";
	EmbeddedKafkaRule embeddedKafka;
	KafkaServer kafkaServer;
	private String tmpKafkaDir;

	@BeforeClass
	public void setup() {
		applicationContext = new AnnotationConfigApplicationContext(KafkaProducerContext.class);
		try {
			startkafkaServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method will start kafka server in memory. Once unit test is completed,
	 * this server will be destroyed.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void startkafkaServer() throws IOException, InterruptedException, ExecutionException {
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
		brokerProps.setProperty("offsets.topic.replication.factor", "1");
		brokerProps.setProperty("broker.id", "1");
		tmpKafkaDir = Files.createTempDirectory("kafkaUtils-").toAbsolutePath().toString();
		brokerProps.setProperty("log.dirs", tmpKafkaDir);
		brokerProps.setProperty("listeners", "PLAINTEXT://" + BROKERHOST + ":" + BROKERPORT);
		KafkaConfig config = new KafkaConfig(brokerProps);
		Time mock = new MockTime();
		kafkaServer = TestUtils.createServer(config, mock);
		kafkaServer.startup();

		// create topics
		AdminClient adminClient = createAdminClient();
		NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
		KafkaFuture<Void> future = adminClient
				.createTopics(Collections.singleton(newTopic), new CreateTopicsOptions().timeoutMs(10000)).all();
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		TopicDescription result = adminClient.describeTopics(Collections.singleton(topicName)).all().get()
				.get(topicName);
		logger.info("Partition Info: " + result.partitions());

	}

	@Test
	public void test() {
		logger.info("Starting Kafka Producer Test");
		KafkaProducerService kafkaProducerService = applicationContext.getBean("kafkaProducerService",
				KafkaProducerService.class);
		kafkaProducerService.init();
		KafkaProducerWrapper<Integer, String> producer = kafkaProducerService.createProducer(topicName);
		String message = "this is a test-";
		int sendCount = 10;
		List<String> producingList = new ArrayList<String>(sendCount);
		for (int i = 0; i < sendCount; i++) {
			KafkaMessageDto<Integer, String> messageDto = KafkaMessageDto.createMessage(i, message + i);
			producingList.add(messageDto.getValue());
			try {
				producer.sendSync(messageDto);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.info("Starting consumer:");
		Consumer<Long, String> consumer = createConsumer();
		final int giveUp = 10;
		int noRecordsCount = 0;
		List<String> consumingList = new ArrayList<String>(sendCount);
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
				logger.info("Consumer Record:({}, {}, {}, {})", record.key(), record.value(), record.partition(),
						record.offset());
				consumingList.add(record.value());
			});

			consumer.commitAsync();
			if (consumingList.size() == sendCount) {
				break;
			}
		}

		Assert.assertEquals(producingList, consumingList);

	}

	private Consumer<Long, String> createConsumer() {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer1");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		// this property is important as this will ensure we are reading all records
		// from begining of the topic
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		// Create the consumer using props.
		final Consumer<Long, String> consumer = new KafkaConsumer<>(props);

		// Subscribe to the topic.
		consumer.subscribe(Collections.singletonList(topicName));
		return consumer;
	}

	private AdminClient createAdminClient() {
		Properties adminProp = new Properties();
		adminProp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091");
		adminProp.put(AdminClientConfig.CLIENT_ID_CONFIG, "test-admin");
		adminProp.put(AdminClientConfig.METADATA_MAX_AGE_CONFIG, "3000");
		AdminClient adminClient = AdminClient.create(adminProp);
		return adminClient;
	}

	@AfterClass
	public void destroy() {
		kafkaServer.shutdown();
		logger.info("Removing Kafka log dir: " + tmpKafkaDir);
		try {
			Files.walk(Paths.get(tmpKafkaDir)).sorted(Comparator.reverseOrder()).map(Path::toFile)
//		    .peek(System.out::println)
					.forEach(File::delete);
		} catch (IOException e) {
			logger.error("Unable to delete : " + tmpKafkaDir, e);
		}
	}
}
