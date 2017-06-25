package com.tiger.dubbo.callback;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ConsumerCallback {
	
	public static final Logger logger = LoggerFactory.getLogger(ConsumerCallback.class);
	
	/**
	 * 功能：接口默认处理方法
	 * @param record	消费者获取到的一条消息
	 */
	default void doAction(ConsumerRecord<String, String> record) {
		logger.info("topic = " + record.topic() + ", partition = " + record.partition() + ", offset = " + record.offset() + ", key = " + record.key() +", value = " + record.value());
	}
	
}
