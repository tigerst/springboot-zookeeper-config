package com.tiger.dubbo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tiger.dubbo.service.DubboService;

@Service("dubboService")
public class DubboServiceImpl implements DubboService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String dealMessage(String message) {
		logger.info("----------" + message);
		return "soa result:" + message;
	}

}
