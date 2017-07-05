package com.tiger.dubbo.config.dubbo;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.tiger.dubbo.service.DubboService;
import com.tiger.dubbo.utils.Constants;

@Configuration
@AutoConfigureAfter(DubboBaseConfig.class) // 在基础加载之后加载
public class ReferenceServiceConfig {

	/**
	 * 功能：引用的服务,version="1.0.0"与提供者一样,关闭某个服务的启动时检查：(没有提供者时报错)check="false"
	 * 
	 * @return
	 */
	@Bean
	public ReferenceBean<DubboService> dubboService1() {
		ReferenceBean<DubboService> ref = new ReferenceBean<DubboService>();
		ref.setVersion(Constants.appConfig.getProperty("dubbo.reference.version")); // dubbo.reference.version
		ref.setInterface(DubboService.class);	//各自方法接口
		ref.setTimeout(5000);	//各自方法调通超时
		ref.setRetries(3);	//各自方法重试次数
		ref.setCheck(false);	//各自方法启动时是否检查
		return ref;
	}

}
