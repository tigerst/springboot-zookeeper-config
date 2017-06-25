package com.tiger.dubbo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.tiger.dubbo.task.CheckZKConfig;
import com.tiger.dubbo.utils.PropertiesUtil;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		PropertiesUtil.loadPoperties();
		SpringApplication application = new SpringApplication(Application.class);
		ConfigurableApplicationContext applicationContext = application.run(args);
		CheckZKConfig checkZKConfig = new CheckZKConfig(application, applicationContext);	//设置应用和上下文
		checkZKConfig.check(args);	//变动校验
		checkZKConfig.scheduleCheck(args);	//定时校验
		String projectPath = System.getProperty("user.dir");	//当前工程路径
		String projectName = projectPath.substring(projectPath.lastIndexOf(File.separator) + 1).replace("-", "");
		logger.info(">>>>>>>>>>>>>>>" + projectName);
		logger.info(">>>>>>>>>>>>" + applicationContext.getId());
		logger.info("Let's inspect the beans provided by Spring Boot:");
	}
	
}