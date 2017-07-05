package com.tiger.dubbo.config.mq;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tiger.dubbo.utils.Constants;

@Configuration
public class KafkaConfig {
	
	/**
	 * 
	 * 获取生产者实例 配置参数说明
	 * bootstrap.servers 
	 * 		Kafka集群连接串，可以由多个host:port组成 
	 * acks(默认为1)
	 * 		broker消息确认的模式，有三种： 
	 * 		0：不进行消息接收确认，即Client端发送完成后不会等待Broker的确认
	 * 		1：由Leader确认，Leader接收到消息后会立即返回确认信息
	 * 		all：集群完整确认，Leader会等待所有in-sync的follower节点都确认收到消息后，再返回确认信息 
	 * retries
	 * 		发送失败时Producer端的重试次数，默认为0 
	 * batch.size
	 * 		当同时有大量消息要向同一个分区发送时，Producer端会将消息打包后进行批量发送。 
	 * 		如果设置为0，则每条消息都独立发送。默认为16384字节
	 * linger.ms(默认为0)
	 * 		发送消息前等待的毫秒数，与batch.size配合使用。
	 * 		在消息负载不高的情况下，配置linger.ms能够让Producer在发送消息前等待一定时间，以积累更多的消息打包发送，达到节省网络资源的目的。
	 * key.serializer 
	 * 		消息key的序列器Class，根据key的类型决定 
	 * value.serializer
	 * 		消息value的序列器Class，根据value的类型决定
	 * buffer.memory(默认33554432字节,即32MB)
	 * 		消息缓冲池大小。尚未被发送的消息会保存在Producer的内存中，如果消息产生的速度大于消息发送的速度，那么缓冲池满后发送消息的请求会被阻塞
	 * 
	 * @return	生产者
	 * @throws Exception
	 * 
	 */
	@Bean("kafkaProducer")
	public Producer<String, String> kafkaProducer() throws Exception {
		Properties props = new Properties();
		//kafka服务器列表-producer and consumer
		props.put("bootstrap.servers", Constants.appConfig.getProperty("bootstrap.servers"));
		//消息确认模式-producer
		props.put("acks", Constants.appConfig.getProperty("acks"));
		//重试次数-producer
		props.put("retries", Constants.appConfig.getProperty("retries"));
		//批次大小-producer
		props.put("batch.size", Constants.appConfig.getProperty("batch.size"));
		//请求队列大小-producer
		props.put("linger.ms", Constants.appConfig.getProperty("linger.ms"));
		//消息缓冲池大小-producer
		props.put("buffer.memory", Constants.appConfig.getProperty("buffer.memory"));
		//key序列化器-producer
		props.put("key.serializer", Constants.appConfig.getProperty("key.serializer"));
		//value序列化器-producer
		props.put("value.serializer", Constants.appConfig.getProperty("value.serializer"));
		
		return new KafkaProducer<String, String>(props);
	}
	
}
