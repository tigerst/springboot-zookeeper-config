package com.tiger.dubbo.utils;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import com.alibaba.fastjson.JSON;
import com.tiger.dubbo.Application;

public class PropertiesUtil {

	/**
	 * 功能：从zookeeper的配置中心加载配置
	 * 
	 * @throws Exception
	 */
	public static void loadPoperties() throws Exception {
		Properties props = new Properties();
		InputStream is = Application.class.getClassLoader().getResourceAsStream("application.properties");
		props.load(is); // 将输入流中的配置加载进Properties
		String path = props.getProperty("appConfig.path");
		Constants.appConfig.putAll(props);	//将现有的配置也加入总配置对象中
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString(props.getProperty("zookeeper.servers"))
				.sessionTimeoutMs(Integer.parseInt(props.getProperty("zookeeper.sessionTimeoutMs")))
				.connectionTimeoutMs(Integer.parseInt(props.getProperty("zookeeper.connectionTimeoutMs")))
				.retryPolicy(
						new ExponentialBackoffRetry(Integer.parseInt(props.getProperty("zookeeper.baseSleepTimeMs")),
								Integer.parseInt(props.getProperty("zookeeper.maxRetries"))))
				.build();
		client.start();
		Stat stat = new Stat();
		List<String> list = client.getChildren().storingStatIn(stat).forPath(path);
		String value = null;
		String childPath = null;
		for (String key : list) {
			childPath = path + "/" + key;
			value = new String(client.getData().forPath(childPath), "UTF-8");
			Constants.appConfig.put(key, value); // 加入配置map中
		}
		Thread.sleep(100);
		client.close();
	}
	
	/**
	 * 功能：将简单json字符串转换成Properties
	 * @param source
	 * @return
	 */
	public static Properties loadJsonObjectStr2Props(String source){
		if(source == null || "".equalsIgnoreCase(source))
			return null;
		Properties props = new Properties();
		props = (Properties) JSON.parseObject(source, Properties.class);
		return props;
	}
	
	public static void main(String[] args) {
		Properties props = loadJsonObjectStr2Props("{\"name\":\"tiger\"}");
		if(props != null){
			System.out.println(props);
		}
	}
	
}
