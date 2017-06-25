package com.tiger.dubbo.config.dubbo;

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
        serviceBean.setProxy(Constants.appConfig.getProperty("dubbo.service.proxy").trim());	//dubbo.service.proxy
        serviceBean.setVersion(Constants.appConfig.getProperty("dubbo.service.version").trim());	//dubbo.service.version
        serviceBean.setInterface(DubboService.class);
        serviceBean.setRef(dubboService);
        String timeout = Constants.appConfig.getProperty("dubbo.service.timeout");
        if(timeout!=null && !"".equalsIgnoreCase(timeout))
        	serviceBean.setTimeout(Integer.parseInt(timeout.trim()));	//dubbo.service.timeout	//超时
        String retries = Constants.appConfig.getProperty("dubbo.service.retries");
        if(retries!=null && !"".equalsIgnoreCase(retries))
        	serviceBean.setRetries(Integer.parseInt(retries.trim()));	//dubbo.service.retries	//重试次数
        return serviceBean;
    }
	
}
