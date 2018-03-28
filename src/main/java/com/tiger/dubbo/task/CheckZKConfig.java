package com.tiger.dubbo.task;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.tiger.dubbo.utils.Constants;

public class CheckZKConfig {

	private static final Logger logger = LoggerFactory.getLogger(CheckZKConfig.class);

	private static final int SCHEDULE_PERIOD_HOUR = 24; // 定时拉取zookeeper配置中心配置的时间间隔为24小时
	
	private volatile SpringApplication application; // 应用

	private volatile ConfigurableApplicationContext applicationContext; // 上下文，容器

	private ExecutorService exec = Executors.newSingleThreadExecutor(); // 自定义节点通知线程池
	
	private Random rd = new Random();

	public CheckZKConfig(SpringApplication application, ConfigurableApplicationContext applicationContext) {
		this.application = application;
		this.applicationContext = applicationContext;
	}

	/**
	 * 功能：实时监听配置信息，并作响应处理 1.启动zookeeper客户端连接监测 2.创建中心配置项目变动通知监听
	 * 3.当配置中心配置变动时，客户端收到通知，重启应用
	 * 
	 * @param args
	 */
	public void check(String[] args) {
		final String path = Constants.appConfig.getProperty("appConfig.path");
		Thread t = new Thread(new CheckZKTask(path, args));
		t.start(); // 启动zookeeper客户端监测，并启动子节点变动通知
	}

	/**
	 * 功能：定时拉取配置中心配置，并作相应处理。此弥补由于网络异常造成实时监听配置失败的情况
	 * 1.每天凌晨定时拉取zookeeper配置中心的项目最新配置 2.校验当前配置与最新配置差异 3.配置有变动则重启应用
	 * 
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public void scheduleCheck(String[] args) {
		ScheduledExecutorService scheduleExec = Executors.newSingleThreadScheduledExecutor(); // 自定义定时任务线程池
		Calendar calendar = Calendar.getInstance();
		//获取路径
		final String path = Constants.appConfig.getProperty("appConfig.path");
		// 距离第二天的小时差 
		int initialDelay = SCHEDULE_PERIOD_HOUR - calendar.HOUR_OF_DAY;
		// 设置并启动定时拉去配置任务
		scheduleExec.scheduleAtFixedRate(new ScheduledCheckTask(path, args), initialDelay, SCHEDULE_PERIOD_HOUR,
				TimeUnit.HOURS); 

//		int initialDelay = 10; // 距离第二天的小时差
//		scheduleExec.scheduleAtFixedRate(new ScheduledCheckTask(path, args), initialDelay, 30, TimeUnit.SECONDS); // 设置并启动定时拉去配置任务
	}

	/**
	 * 功能：修改当前项目配置
	 * 
	 * @param path
	 * @param client
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	private static boolean updateConfig(final String path, CuratorFramework client) {
		boolean isChanged = false;
		try {
			Stat stat = new Stat();
			List<String> list = client.getChildren().storingStatIn(stat).forPath(path);
			String value = null;
			String childPath = null;
			for (String key : list) {
				childPath = path + "/" + key;
				value = new String(client.getData().forPath(childPath), "UTF-8");
				if (value.trim().equalsIgnoreCase(Constants.appConfig.getProperty(key).trim())) {
					continue; // 相同则跳过
				}
				Constants.appConfig.setProperty(key, value); // 加入配置map中
				isChanged = true;
			}
			return isChanged;
		} catch (UnsupportedEncodingException e) {
			logger.error("检验配置异常", e);
			return false; // 失败
		} catch (Exception e) {
			logger.error("检验配置异常", e);
			return false; // 失败
		}
	}

	/**
	 * 功能：重启应用，refresh不可用，所以才去重启的方式 1.先关闭上下文，释放所有的资源和锁，并销毁缓存的单例bean
	 * 2.再启动上下文，加载spring bean
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	private void reStartProject(String[] args) throws InterruptedException {
		try {
			Thread.sleep((1 + rd.nextInt(10)) * 1000); // 线程休眠[1，10]秒时间，防止集群中的应用同时重启，应用不可用
			applicationContext.close(); // 关闭上下文，释放所有的资源和锁，并销毁缓存的单例bean
			Thread.sleep(100); // 线程休眠100ms
			applicationContext = application.run(args); // 重启
			String projectPath = System.getProperty("user.dir");	//当前工程路径
			String projectName = projectPath.substring(projectPath.lastIndexOf(File.separator) + 1).replace("-", "");
			logger.info(">>>>>>>>>>>>>>>" + projectName);
			logger.info(">>>>>>>>>>>>" + applicationContext.getId());
			logger.info("The application restart successfully");
		} catch (Exception e) {
			logger.error("重启应用异常", e);
		}
	}

	/**
	 * 功能： 1.检测zookeeper客户端连接，不存在则创建，存在则线程休眠 2.添加配置变化监听器
	 * 
	 * @ClassName: Application.java
	 * @Description: 内部类，检测zookeeper客户端连接，不存在则创建，存在则线程休眠
	 * @author: Tiger
	 * @date: 2017年6月5日 上午10:16:56
	 *
	 */
	private class CheckZKTask implements Runnable {

		private final String path;

		private final String[] args;

		public CheckZKTask(String path, String[] args) {
			this.path = path;
			this.args = args;
		}

		@Override
		public void run() {
			CuratorFramework client = null;
			PathChildrenCache cache = null;
			try {
				while (true) {
					logger.debug("检测zookeeper客户端连接...");
					// client存在则使用现有的，不存在则创建
					if (client == null) {
						logger.debug("zookeeper客户端不存在，创建客户端连接...");
						client = CuratorFrameworkFactory.builder()
								.connectString(Constants.appConfig.getProperty("zookeeper.servers"))
								.sessionTimeoutMs(
										Integer.parseInt(Constants.appConfig.getProperty("zookeeper.sessionTimeoutMs")))
								.connectionTimeoutMs(Integer
										.parseInt(Constants.appConfig.getProperty("zookeeper.connectionTimeoutMs")))
								.retryPolicy(new ExponentialBackoffRetry(
										Integer.parseInt(Constants.appConfig.getProperty("zookeeper.baseSleepTimeMs")),
										Integer.parseInt(Constants.appConfig.getProperty("zookeeper.maxRetries"))))
								.build();
						client.start();
						cache = new PathChildrenCache(client, path, false);
						cache.start(StartMode.POST_INITIALIZED_EVENT);
						cache.getListenable().addListener(new Callback(path, args), exec);
					}
					logger.debug("检测zookeeper客户端端连接结束！");
					Thread.sleep(60000); // 每个60s检测一次client是否可用
				}
			} catch (InterruptedException e) {
				logger.error("检测zookeeper客户端连接异常", e);
			} catch (Exception e) {
				logger.error("检测zookeeper客户端连接异常", e);
			}
		}

	}

	/**
	 * 
	 * @ClassName: Application.java
	 * @Description: 内部类，监听子节点变动情况，并作相应处理
	 * @author: Tiger
	 * @date: 2017年6月5日 上午9:23:42
	 *
	 */
	private class Callback implements PathChildrenCacheListener {

		private final String path;

		private final String[] args;

		public Callback(String path, String[] args) {
			this.path = path;
			this.args = args;
		}

		@Override
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
			logger.debug("监听配置...");
			switch (event.getType()) {
			case CHILD_ADDED:
				if (updateConfig(path, client))
					reStartProject(args);
				logger.debug("监听配置结束");
				break;

			case CHILD_UPDATED:
				if (updateConfig(path, client))
					reStartProject(args);
				logger.debug("监听配置结束");
				break;

			case CHILD_REMOVED:
				if (updateConfig(path, client))
					reStartProject(args);
				logger.debug("监听配置结束");
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 功能： 1.拉去zookeeper配置中的配置 2.配置变化 3.如果配置有变动，则重启应用
	 * 
	 * @ClassName: CheckZKConfig.java
	 * @Description:
	 * @author: Tiger
	 * @date: 2017年6月5日 下午12:38:47
	 *
	 */
	private class ScheduledCheckTask implements Runnable {

		private final String path;

		private final String[] args;

		public ScheduledCheckTask(String path, String[] args) {
			this.path = path;
			this.args = args;
		}

		@Override
		public void run() {
			try {
				logger.debug("定时拉去配置中心配置...");
				// 创建客户端
				CuratorFramework client = CuratorFrameworkFactory.builder()
						.connectString(Constants.appConfig.getProperty("zookeeper.servers"))
						.sessionTimeoutMs(
								Integer.parseInt(Constants.appConfig.getProperty("zookeeper.sessionTimeoutMs")))
						.connectionTimeoutMs(
								Integer.parseInt(Constants.appConfig.getProperty("zookeeper.connectionTimeoutMs")))
						.retryPolicy(new ExponentialBackoffRetry(
								Integer.parseInt(Constants.appConfig.getProperty("zookeeper.baseSleepTimeMs")),
								Integer.parseInt(Constants.appConfig.getProperty("zookeeper.maxRetries"))))
						.build();
				client.start(); // 启动连接
				if (updateConfig(path, client)) // 校验配置中心的项目配置与当前项目的配置比较，不一样则重启应用
					reStartProject(args);
				client.close(); // 处理完任务，关闭客户端
			} catch (Exception e) {
				logger.error("定时拉取配置异常", e);
			} finally {
				logger.debug("定时拉取配置，并重启应用结束！");
			}
		}
	}

	public SpringApplication getApplication() {
		return application;
	}

	public ConfigurableApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplication(SpringApplication application) {
		this.application = application;
	}

	public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
