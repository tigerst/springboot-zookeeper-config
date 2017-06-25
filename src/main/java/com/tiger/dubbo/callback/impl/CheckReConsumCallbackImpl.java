package com.tiger.dubbo.callback.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import com.tiger.dubbo.callback.CheckReConsumCallback;

@Component("checkReConsumCallback")
public class CheckReConsumCallbackImpl implements CheckReConsumCallback {

	/**
	 * 功能：接口默认处理方法
	 * @param record
	 */
	public void doCheck(ConsumerRecord<String, String> record){
		logger.info("reconsum the record>>>>>>topic = " + record.topic() + ", offset = " + record.offset() + ", key = " + record.key() +", value = " + record.value());
		if(record.value().contains("tiger")){	//模拟处理失败情况
			throw new RuntimeException("消息包含了敏感人物名字");
		}
	}
	
}
