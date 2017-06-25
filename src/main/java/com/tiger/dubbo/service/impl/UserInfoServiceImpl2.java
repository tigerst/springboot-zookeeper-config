package com.tiger.dubbo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiger.dubbo.config.multidatasource.DataSource;
import com.tiger.dubbo.dao.UserInfoMapper;
import com.tiger.dubbo.model.UserInfo;
import com.tiger.dubbo.service.UserInfoService2;
import com.tiger.dubbo.utils.Constants;

@Service("userInfoService2")
public class UserInfoServiceImpl2 implements UserInfoService2 {
	
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@DataSource(value = Constants.SLAVE_DATASOURCE_CODE)
	@Transactional(readOnly = false, rollbackFor=Exception.class)
	public void save() {
		UserInfo userInfo = new UserInfo();
		userInfo.setName("123");
		userInfo.setId(20L);
		userInfoMapper.insert(userInfo);
	}
	
}
