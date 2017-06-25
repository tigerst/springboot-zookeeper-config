package com.tiger.dubbo.task;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tiger.dubbo.callback.CheckReConsumCallback;
import com.tiger.dubbo.utils.Constants;

@Component("checkReConsumQueue")
public class CheckerTask implements InitializingBean {

	@Autowired
	private CheckReConsumCallback checkReConsumCallback; // 检擦异常消息队列回调接口

	@Override
	public void afterPropertiesSet() throws Exception {
		CheckerGroup checkerGroup = new CheckerGroup(checkReConsumCallback, Constants.appConfig.getProperty("consum.errmsg.dir").trim());
		checkerGroup.buildAndRunCheckers(1);	//创建检查任务，并启动线程
	}

}
