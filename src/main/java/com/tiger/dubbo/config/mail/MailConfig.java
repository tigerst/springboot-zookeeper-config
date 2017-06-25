package com.tiger.dubbo.config.mail;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.tiger.dubbo.utils.Constants;

@Configuration
public class MailConfig {
	
	private static final String encoding = "UTF-8";
	
	@Bean("mailSender")
	public JavaMailSender mailSender() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setDefaultEncoding(encoding);
		javaMailSenderImpl.setHost(Constants.appConfig.getProperty("mail.host").trim());
		javaMailSenderImpl.setUsername(Constants.appConfig.getProperty("mail.username").trim());
		javaMailSenderImpl.setPassword(Constants.appConfig.getProperty("mail.password").trim());
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", Constants.appConfig.getProperty("mail.smtp.auth").trim());
		properties.setProperty("mail.smtp.ssl.enable", Constants.appConfig.getProperty("mail.smtp.ssl.enable").trim());
		if("true".equalsIgnoreCase(Constants.appConfig.getProperty("mail.smtp.ssl.enable").trim())){
			properties.setProperty("mail.smtp.socketFactory.class" , "javax.net.ssl.SSLSocketFactory");
			properties.setProperty("mail.smtp.socketFactory.fallback", "false");
			properties.setProperty("mail.smtp.socketFactory.port", Constants.appConfig.getProperty("mail.smtp.socketFactory.port").trim());
		}
		properties.setProperty("mail.transport.protocol", Constants.appConfig.getProperty("mail.transport.protocol").trim());
		properties.setProperty("mail.debug", "false");
		properties.setProperty("mail.smtp.timeout", Constants.appConfig.getProperty("mail.smtp.timeout").trim());
		javaMailSenderImpl.setJavaMailProperties(properties);
		return javaMailSenderImpl;
	}
	
}
