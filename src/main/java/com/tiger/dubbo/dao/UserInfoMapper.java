package com.tiger.dubbo.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.tiger.dubbo.model.UserInfo;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    
    @Select("select fui.* from finance_userinfo fui where fui.NAME = #{name,jdbcType=VARCHAR}")
    @ResultMap(value = { "BaseResultMap" })
    UserInfo selectByName(@Param(value = "name") String name);
}