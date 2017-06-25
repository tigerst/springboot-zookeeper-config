package com.tiger.dubbo.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tiger.dubbo.callback.CheckReConsumCallback;
import com.tiger.dubbo.utils.Constants;
import com.tiger.dubbo.utils.TempFileUtil;

public class CheckerGroup {

	private ExecutorService exec;

	private List<CheckRunner> taskList = new ArrayList<CheckRunner>(); // 任务列表

	private final CheckReConsumCallback callback; // 检擦异常消息队列回调接口

	private final String basePath; // check任务失败时记录的日志文件的基路径

	public CheckerGroup(CheckReConsumCallback callback, String basePath) {
		this.callback = callback;
		this.basePath = basePath;
	}

	public void buildAndRunCheckers(int groupSize) throws Exception {
		if (groupSize <= 0) {
			exec = Executors.newSingleThreadExecutor();
			groupSize = 1; // 小于等于0,重新赋值为1
		} else
			exec = Executors.newFixedThreadPool(groupSize);
		CheckRunner checkRunner;
		for (int i = 1; i <= groupSize; i++) {
			checkRunner = new CheckRunner(); // 创建check任务
			exec.submit(checkRunner); // 提交任务到线程池中
			taskList.add(checkRunner); // 加入任务列表中
		}
	}

	private class CheckRunner implements Runnable {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final AtomicBoolean closed = new AtomicBoolean(false);

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!closed.get()) { // 运行标记
				ConsumerRecord<String, String> record = Constants.reConsumeMsgQueue.poll(); // 从重新消费队列中取数据
				if (null != record) {
					// 根据不同的topic做不同的处理
					try {
						callback.doCheck(record);
					} catch (Exception e) {
						// 记录错误日志文件中
						String fileName = basePath.endsWith("/") ? basePath + record.topic()
								: basePath + "/" + record.topic() + ".log";
						JsonParser parser = new JsonParser();
						JsonElement root = parser.parse(record.value());	//此处使用gson，fastjson有问题
						String jsonString = null;
						if (root.isJsonObject() || root.isJsonArray()) {	//value为json对象
							jsonString = "{\"topic\":\"" + record.topic() + "\",\"partition\":\"" + record.partition()
									+ "\",\"offset\":\"" + record.offset() + "\",\"key\":\"" + record.key()
									+ "\",\"value\":" + record.value() + "}\n"; // 组装json字符串
						} else {	//value不为json对象
							jsonString = "{\"topic\":\"" + record.topic() + "\",\"partition\":\"" + record.partition()
									+ "\",\"offset\":\"" + record.offset() + "\",\"key\":\"" + record.key()
									+ "\",\"value\":\"" + record.value() + "\"}\n"; // 组装json字符串
						}
						TempFileUtil.wirteFile(fileName, jsonString); // 记录到日志文件中
						logger.error("检查处理失败", e);
					}
				}
			}
		}

	}

	/**
	 * 功能：关闭消费者组
	 */
	public void close() {
		if (taskList != null && taskList.size() > 0) {
			for (CheckRunner consumerRunner : taskList) {
				consumerRunner.closed.set(true);
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
		exec.shutdown();
	}

	public List<CheckRunner> getTaskList() {
		return taskList;
	}

	public CheckReConsumCallback getCallback() {
		return callback;
	}

	public String getBasePath() {
		return basePath;
	}

}
