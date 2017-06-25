package com.tiger.dubbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tiger.dubbo.callback.ProducerCallback;
import com.tiger.dubbo.utils.Constants;
import com.tiger.dubbo.utils.KafkaProducerUtil;

@RestController
@RequestMapping("dubbo-service/kafkaTest")
public class KafkaTestController {

	@Autowired
	@Qualifier("kafkaProducerUtil")
	private KafkaProducerUtil kafkaProducerUtil;

	@Autowired
	@Qualifier("producerCommonCallback")
	private ProducerCallback producerCommonCallback;

	@RequestMapping(value = "producer", method = RequestMethod.POST)
	public String producer(@RequestBody JSONObject jsonObject) {
		if (jsonObject.getString("name").contains("tiger1"))
			kafkaProducerUtil.send(Constants.appConfig.getProperty("dubbo_core.topics").trim(), jsonObject.getString("name"), producerCommonCallback); // 生产者生产消息
		else
			kafkaProducerUtil.send(Constants.appConfig.getProperty("dubbo_core.topics").trim(), jsonObject.toJSONString(), producerCommonCallback); // 生产者生产消息
		return "send message successfully";
	}

}
