package com.tiger.dubbo.config.dubbo;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.spring.ServiceBean;
import com.tiger.dubbo.service.DubboService;
import com.tiger.dubbo.utils.Constants;

@Configuration
@AutoConfigureAfter(DubboBaseConfig.class)	//在基础加载之后加载
public class ExportServiceConfig {

	/**
	 * 功能：声明需要暴露的服务接口
	 * @param dubboService
	 * @return
	 */
	@Bean
    public ServiceBean<DubboService> dubboServiceExport(@Autowired @Qualifier("dubboService") DubboService dubboService) {
        ServiceBean<DubboService> serviceBean = new ServiceBean<DubboService>();
        serviceBean.setProxy(Constants.appConfig.getProperty("dubbo.service.proxy"));	//dubbo.service.proxy
        serviceBean.setVersion(Constants.appConfig.getProperty("dubbo.service.version"));	//dubbo.service.version
        serviceBean.setInterface(DubboService.class);
        serviceBean.setRef(dubboService);
        serviceBean.setTimeout(NumberUtils.toInt(Constants.appConfig.getProperty("dubbo.service.timeout"), 5000));	//dubbo.service.timeout	//超时
        serviceBean.setRetries(NumberUtils.toInt(Constants.appConfig.getProperty("dubbo.service.retries"), 3));	//dubbo.service.retries	//重试次数
        return serviceBean;
    }
	
}
