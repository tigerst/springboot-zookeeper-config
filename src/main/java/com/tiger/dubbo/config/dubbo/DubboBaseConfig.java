package com.tiger.dubbo.config.dubbo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.tiger.dubbo.utils.Constants;

@Configuration
public class DubboBaseConfig {

	/**
	 * 功能：使用zookeeper注册中心暴露服务地址 ，关闭注册中心启动时检查(注册订阅失败时报错)check="false"
	 * 
	 * @return
	 */
	@Bean
	public RegistryConfig registry() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(Constants.appConfig.getProperty("dubbo.registry.address").trim()); // dubbo.registry.address
		registryConfig.setProtocol(Constants.appConfig.getProperty("dubbo.registry.protocol").trim()); // dubbo.registry.protocol
		return registryConfig;
	}

	/**
	 * 功能：提供方应用信息，用于计算依赖关系
	 * 
	 * @return
	 */
	@Bean
	public ApplicationConfig dubboApplication() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(Constants.appConfig.getProperty("dubbo.application.name").trim()); // dubbo.application.name
		applicationConfig.setOwner(Constants.appConfig.getProperty("dubbo.application.owner").trim()); // dubbo.application.owner
		applicationConfig.setOrganization(Constants.appConfig.getProperty("dubbo.application.organization").trim()); // dubbo.application.organization
		return applicationConfig;
	}

	/**
	 * 功能：用dubbo协议在20880端口暴露服务
	 * 
	 * @return
	 */
	@Bean
	public ProtocolConfig protocol() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName(Constants.appConfig.getProperty("dubbo.protocol.name").trim()); // dubbo.protocol.name
		protocolConfig.setPort(NumberUtils.toInt(Constants.appConfig.getProperty("dubbo.protocol.port").trim(), 30280)); // dubbo.protocol.port
		protocolConfig.setSerialization(Constants.appConfig.getProperty("dubbo.protocol.serialization").trim()); // dubbo.protocol.serialization
		String threadpool = Constants.appConfig.getProperty("dubbo.protocol.threadpool").trim();
		protocolConfig.setThreadpool(threadpool); // dubbo.protocol.threadpool
													// //线程池类型 fixed/cached
		if ("fixed".equalsIgnoreCase(threadpool)) // 固定线程池时设置线程池大小
			protocolConfig.setThreads(
					NumberUtils.toInt(Constants.appConfig.getProperty("dubbo.protocol.threads").trim(), 200)); // dubbo.protocol.threads
																												// //服务线程池大小(固定大小)
		String iothreads = Constants.appConfig.getProperty("dubbo.protocol.iothreads");
		if (StringUtils.isNotEmpty(iothreads))
			protocolConfig.setIothreads(NumberUtils.toInt(iothreads)); // dubbo.protocol.iothreads
																		// //io线程池大小(固定大小),默认为cpu个数+1

		protocolConfig.setBuffer(NumberUtils.toInt(Constants.appConfig.getProperty("dubbo.protocol.buffer").trim())); // dubbo.protocol.buffer
																														// //网络读写缓冲区大小
		protocolConfig
				.setHeartbeat(NumberUtils.toInt(Constants.appConfig.getProperty("dubbo.protocol.heartbeat").trim())); // dubbo.protocol.heartbeat
																														// //心跳间隔，对于长连接，当物理层断开时，比如拔网线，TCP的FIN消息来不及发送，对方收不到断开事件，此时需要心跳来帮助检查连接是否已断开
		return protocolConfig;
	}

}
