package com.tiger.dubbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tiger.dubbo.callback.ProducerCallback;
import com.tiger.dubbo.utils.RedisUtil;

@RestController
@RequestMapping("dubbo-service/redisTest")
public class RedisTestController {

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	@Qualifier("producerCommonCallback")
	private ProducerCallback producerCommonCallback;

	@RequestMapping(value = "redisCache", method = RequestMethod.POST)
	public String producer(@RequestBody JSONObject jsonObject) {
		redisUtil.setCacheObject("redisCache", jsonObject);
		return "success";
	}
	

}
