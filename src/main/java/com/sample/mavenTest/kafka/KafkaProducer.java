package com.sample.mavenTest.kafka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

/**
 * 1、启动kafka中自带的应急的单节点zookeeper服务器：
 * 		bin\windows\zookeeper-server-start.bat config\zookeeper.properties
 * 
 * 2、启动kafka服务器：
 * 		bin\windows\kafka-server-start.bat config\server.properties
 * 
 * 3、创建一个叫"test"的topic，该topic仅有一个partition和一个replica：
 * 		bin\windows\kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
 * 
 * 4、依次启动KafkaProducer和KafkaConsumer，分别进行消息的发送和接收
 * 
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
		new KafkaProducer("test").start();// 使用kafka集群中创建好的主题 test 		
	}

}
