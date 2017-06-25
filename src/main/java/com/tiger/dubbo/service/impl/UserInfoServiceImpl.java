package com.tiger.dubbo.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiger.dubbo.config.multidatasource.DataSource;
import com.tiger.dubbo.dao.UserInfoMapper;
import com.tiger.dubbo.model.UserInfo;
import com.tiger.dubbo.service.UserInfoService;
import com.tiger.dubbo.service.UserInfoService2;
import com.tiger.dubbo.utils.Constants;
import com.tiger.dubbo.utils.RedisUtil;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
	
	@Autowired
	@Qualifier("redisUtil")
	private RedisUtil redisUtil;
	
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Autowired
	private UserInfoService2 userInfoService2;
	
	@Override
	public UserInfo findByName(String name) {
		UserInfo userInfo = redisUtil.getCacheObject(name, UserInfo.class);
		if(userInfo == null) {	//redis中不存在，则从从数据库中查询
			userInfo = userInfoMapper.selectByName(name);
			if(userInfo != null)	//结果不为null时缓存到redis中
				redisUtil.setCacheObject(name, userInfo);
		}
		return userInfo;
	}
	
	@Override
	public UserInfo findById(long id) {
		try {
			UserInfo userInfo = null;
			if(!redisUtil.exist("user_info")){
				userInfo = userInfoMapper.selectByPrimaryKey(id);
				redisUtil.setCacheList("user_info", Arrays.asList(userInfo,userInfo));
			} else {
				if((redisUtil.getCacheList("user_info", UserInfo.class).get(0)).getId() == id){
					return redisUtil.getCacheList("user_info", UserInfo.class).get(0);
				}
				redisUtil.setCacheList("user_info", Arrays.asList(userInfo));
			}
			return userInfo;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	@DataSource(value = Constants.MASTER_DATASOURCE)
	@Transactional(readOnly = false, rollbackFor=Exception.class)
	public void save() {
		userInfoService2.save();
		UserInfo userInfo = new UserInfo();
		userInfo.setName("456");
		userInfo.setId(22L);
		userInfoMapper.insert(userInfo);
		throw new RuntimeException("AAAAAAAAA");
	}
	
}
