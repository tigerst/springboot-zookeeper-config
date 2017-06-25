package com.tiger.dubbo.service;

import com.tiger.dubbo.model.UserInfo;

public interface UserInfoService {

	/**
	 * 根据姓名查找用户信息
	 * @param name
	 * @return
	 */
	public UserInfo findByName(String name);

	UserInfo findById(long id);
	
	public void save();
	
}
