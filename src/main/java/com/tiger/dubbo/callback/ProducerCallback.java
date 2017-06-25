package com.tiger.dubbo.callback;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能：继承Callback接口，将onCompletion方法改写为有默认方法体的接口方法
 * @author Tiger
 *
 */
public interface ProducerCallback extends Callback {
	
	public static final Logger logger = LoggerFactory.getLogger(ProducerCallback.class);
	
	default void onCompletion(RecordMetadata metadata, Exception exception) {
		if (exception != null) {
			logger.error("发送消息异常", exception);
		} else {	//异常为null则表明发送成功
			logger.info("message send to partition " + metadata.partition() + ", offset: " + metadata.offset() + " successfully");
		}
	}
}
