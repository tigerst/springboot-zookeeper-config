package com.tiger.dubbo.utils;

import java.util.UUID;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kafkaProducerUtil")
public class KafkaProducerUtil {
	
	@Autowired
	private Producer<String, String> producer;

	/**
	 * 功能：发送消息
	 * @param topic	主题
	 * @param message	消息
	 */
	public void send(String topic, String message, Callback callback) {
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, topic + UUID.randomUUID(),
				message);
		producer.send(record, callback);
	}
	
	public void closeProducer() {
		producer.close();
	}

}
