package com.tiger.dubbo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tiger.dubbo.service.DubboService;
import com.tiger.dubbo.utils.Constants;

import redis.clients.jedis.JedisPoolConfig;

@RestController
@RequestMapping("dubbo-service/soaTest")
public class DubboController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("jedisPoolConfig")
	private JedisPoolConfig jedisPoolConfig;
	
	@Autowired
	@Qualifier("dubboService1")	//dubboService1为soa引入的bean
	private DubboService dubboService;
	
	@RequestMapping(value = "soaTest", method = RequestMethod.POST)
	public String soaTest(@RequestBody JSONObject jsonObj) {
		try {
			/*return */dubboService.dealMessage(jsonObj.getString("id"));	//远程调用soa
			return Constants.appConfig.getProperty("redis.pool.maxTotal") + "     " + jedisPoolConfig.getMaxTotal();
		} catch (Exception e) {
			logger.error("异常信息" ,e);
		}
		return null;
	}
}
