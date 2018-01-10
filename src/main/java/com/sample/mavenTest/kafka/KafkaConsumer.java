package com.sample.mavenTest.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * 阻塞消费
 * 默认情况下，未设置consumer.timeout.ms参数，ConsumerIterator.hasNext()是阻塞式的，有数据时，ConsumerIterator.hasNext()返回true，无数据时，程序卡在ConsumerIterator.hasNext()，直到有新的数据进来。
 * 如果设置consumer.timeout.ms，则ConsumerIterator.hasNext()是限时消费的，有数据的时候，ConsumerIterator.hasNext()返回true，无数据时，ConsumerIterator.hasNext()等待consumer.timeout.ms时间，如果仍无数据，则抛出ConsumerTimeoutException错误。
 * 
 * KafkaStream是kafka的consumer，KafkaStream的数量应和partition的数量保持一致，一个partition对应一个KafkaStream，超过partition数量的KafkaStream其实一直阻塞着，并不会其作用。
 * 所以，要增加KafkaStream就必须相应增加partition。
 * 
 * offset是consumer消费位置的标志，提交offset表明消息被消费。
 * 自动提交方式，offset是在iterator.next()之后就会进行。
 * 手动提交方式，可以定制消息被后续处理流程处理后再提交，从而确保消息发送的exactly once。如上例，设置auto.commit.enable为false，在print之后手动提交consumerConnector.commitOffsets()，可以确保print每一条消息。
 * 
 * group.id决定了consumer所属的消费组，同一个消费组的consumer共同维护一个offset。
 * 
 * @date 2018年1月5日
 */
public class KafkaConsumer extends Thread{

	private String topic;
	
	public KafkaConsumer(String topic){
		super();
		this.topic = topic;
	}	
	
	@Override
	public void run() {
		ConsumerConnector consumer = createConsumer();
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1); // 一次从主题中获取一个数据
		Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);// 获取每次接收到的这个数据
		ConsumerIterator<byte[], byte[]> iterator =  stream.iterator();
		while(iterator.hasNext()){
			String message = new String(iterator.next().message());
			System.out.println("接收到: " + message);
		}
	}

	private ConsumerConnector createConsumer() {
		Properties properties = new Properties();
		//properties.put("zookeeper.connect", "192.168.1.110:2181,192.168.1.111:2181,192.168.1.112:2181");//声明zk
		properties.put("zookeeper.connect", "127.0.0.1:2181");
		properties.put("group.id", "group1");
		return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
	}	
	
	public static void main(String[] args) {
		//new KafkaConsumer("test").start();// 使用kafka集群中创建好的主题 test 	
		new KafkaConsumer("my-replicated-topic").start();
	}
}
