package com.tiger.dubbo.callback;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能：该接口用于处理消费异常的队列中的消息
 * @author Tiger
 *
 */
public interface CheckReConsumCallback {
	
	public static final Logger logger = LoggerFactory.getLogger(CheckReConsumCallback.class);
	
	/**
	 * 功能：接口默认处理方法
	 * @param record
	 */
	default void doCheck(ConsumerRecord<String, String> record) {
		logger.info("reconsum the record>>>>>>topic = " + record.topic() + ", offset = " + record.offset() + ", key = " + record.key() +", value = " + record.value());
	}
	
}
