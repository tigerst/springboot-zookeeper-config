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
				Constants.appConfig.getProperty("spring.druid.view"));
		servletRegistrationBean.addInitParameter("resetEnable", Constants.appConfig.getProperty("spring.druid.resetEnable"));
		servletRegistrationBean.addInitParameter("allow", Constants.appConfig.getProperty("spring.druid.allow"));
		servletRegistrationBean.addInitParameter("deny", Constants.appConfig.getProperty("spring.druid.deny"));
		servletRegistrationBean.addInitParameter("loginUsername", Constants.appConfig.getProperty("spring.druid.loginUsername"));
		servletRegistrationBean.addInitParameter("loginPassword", Constants.appConfig.getProperty("spring.druid.loginPassword"));

		return servletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns(Constants.appConfig.getProperty("spring.druid.urlPatterns"));
		filterRegistrationBean.addInitParameter("exclusions", Constants.appConfig.getProperty("spring.druid.exclusions"));
		filterRegistrationBean.addInitParameter("profileEnable", Constants.appConfig.getProperty("spring.druid.profileEnable"));

		return filterRegistrationBean;
	}
}