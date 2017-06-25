package com.tiger.dubbo.config.druid;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.tiger.dubbo.utils.Constants;

@Configuration
public class DruidConfig {

	@Bean
	public ServletRegistrationBean druidServlet() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),
				Constants.appConfig.getProperty("spring.druid.view").trim());
		servletRegistrationBean.addInitParameter("resetEnable", Constants.appConfig.getProperty("spring.druid.resetEnable").trim());
		servletRegistrationBean.addInitParameter("allow", Constants.appConfig.getProperty("spring.druid.allow").trim());
		servletRegistrationBean.addInitParameter("deny", Constants.appConfig.getProperty("spring.druid.deny").trim());
		servletRegistrationBean.addInitParameter("loginUsername", Constants.appConfig.getProperty("spring.druid.loginUsername").trim());
		servletRegistrationBean.addInitParameter("loginPassword", Constants.appConfig.getProperty("spring.druid.loginPassword").trim());

		return servletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns(Constants.appConfig.getProperty("spring.druid.urlPatterns").trim());
		filterRegistrationBean.addInitParameter("exclusions", Constants.appConfig.getProperty("spring.druid.exclusions").trim());
		filterRegistrationBean.addInitParameter("profileEnable", Constants.appConfig.getProperty("spring.druid.profileEnable").trim());

		return filterRegistrationBean;
	}
}