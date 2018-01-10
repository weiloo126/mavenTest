package com.sample.mavenTest.kafka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

/**
 * 1、启动kafka中自带的应急的单节点zookeeper服务器：
 * 		> bin\windows\zookeeper-server-start.bat config\zookeeper.properties
 * 
 * 2、启动kafka服务器：
 * 		> bin\windows\kafka-server-start.bat config\server.properties
 * 
 * 3、创建一个叫"test"的topic，该topic仅有一个partition和一个replica：
 * 		> bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
 * 
 * 4、依次启动KafkaProducer和KafkaConsumer，分别进行消息的发送和接收
 * 
 * 5、创建一个multi-broker的kafka集群（对于kafka而言，single-broker就相当于一个节点的集群，因此除了多启动几个broker实例，不需要做多的改变）
 * 	（1）拷贝两份kafka配置文件：
 * 		> copy config\server.properties config\server-1.properties
 * 		> copy config\server.properties config\server-2.properties
 * 	（2）将拷贝的文件作如下修改：
 * 		config/server-1.properties:
 * 		broker.id=1
 * 		listeners=PLAINTEXT://:9093
 * 		log.dir=/tmp/kafka-logs-1
 * 
 * 		config/server-2.properties:
 * 		broker.id=2
 * 		listeners=PLAINTEXT://:9094
 * 		log.dir=/tmp/kafka-logs-2
 * 	（3）创建一个新的topic，其中replication-factor为3：
 * 		> bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
 * 
 * 6、在broker集群下，查看各broker的工作情况，可用“describe topics”命令
 * 		> bin\windows\kafka-topics.bat --describe --zookeeper localhost:2181 --topic my-replicated-topic
 * @date 2018年1月5日
 */
public class KafkaProducer extends Thread{

	private String topic;
	
	public KafkaProducer(String topic){
		super();
		this.topic = topic;
	}	
	
	@Override
	public void run() {
		Producer<Integer, String> producer = createProducer();
		int i=0;
		while(true){
			producer.send(new KeyedMessage<Integer, String>(topic, "message: " + i));
			System.out.println("发送了: " + i);
			try {
				TimeUnit.SECONDS.sleep(1);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Producer<Integer, String> createProducer() {
		Properties properties = new Properties();
		//properties.put("zookeeper.connect", "192.168.1.110:2181,192.168.1.111:2181,192.168.1.112:2181");//声明zk
		properties.put("zookeeper.connect", "127.0.0.1:2181");
		properties.put("serializer.class", StringEncoder.class.getName());
		properties.put("metadata.broker.list", "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094");// 声明kafka broker
		return new Producer<Integer, String>(new ProducerConfig(properties));
	 }	
	
	public static void main(String[] args) {
		//new KafkaProducer("test").start();// 使用kafka集群中创建好的主题 test 	
		new KafkaProducer("my-replicated-topic").start();
	}

}
