package com.tiger.dubbo.task;

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tiger.dubbo.callback.impl.DubboConsumerCallback;
import com.tiger.dubbo.utils.Constants;

@Component("kafkaConsumerGrpTask")
public class ConsumerGroupTask implements InitializingBean {
	
	@Autowired
	@Qualifier("dubboConsumerCallback")
	private DubboConsumerCallback dubboConsumerCallback;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		/*
		 * 设置消费者通用Properties属性
		 */
		Properties props = new Properties();
		props.setProperty("group.id", Constants.appConfig.getProperty("group.id").trim());
		props.setProperty("bootstrap.servers", Constants.appConfig.getProperty("bootstrap.servers").trim());
		props.setProperty("enable.auto.commit", Constants.appConfig.getProperty("enable.auto.commit").trim());
		props.setProperty("auto.commit.interval.ms", Constants.appConfig.getProperty("auto.commit.interval.ms").trim());
		props.setProperty("dubbo_core.topics", Constants.appConfig.getProperty("dubbo_core.topics").trim());
		props.setProperty("key.deserializer", Constants.appConfig.getProperty("key.deserializer").trim());
		props.setProperty("value.deserializer", Constants.appConfig.getProperty("value.deserializer").trim());
		
		//获取消费组topic，并创建消费者组
		ConsumerGroup group = new ConsumerGroup(Constants.appConfig.getProperty("spring.test.producer.topic").trim(), dubboConsumerCallback, props);
		group.setTimeout(Integer.parseInt(Constants.appConfig.getProperty("consumer.poll.timeout").trim()));	//设置poll超时
		group.buildAndRunConsumers(Integer.parseInt(Constants.appConfig.getProperty("consumer.thread.nums").trim()));	//传入消费者数，并启动消费者线程
	}
}
