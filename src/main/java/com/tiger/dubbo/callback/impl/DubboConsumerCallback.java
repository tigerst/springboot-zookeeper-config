package com.tiger.dubbo.callback.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import com.tiger.dubbo.callback.ConsumerCallback;

@Component("dubboConsumerCallback")
public class DubboConsumerCallback implements ConsumerCallback {
	
	/**
	 * 功能：接口默认处理方法
	 * @param record	消费者获取到的一条消息
	 */
	public void doAction(ConsumerRecord<String, String> record) {
		logger.info("topic = " + record.topic() + ", partition = " + record.partition() + ", offset = " + record.offset() + ", key = " + record.key() +", value = " + record.value());
		if(record.value().contains("tiger")){	//模拟处理失败情况
			throw new RuntimeException("消息包含了敏感人物名字");
		}
	}
	
}
