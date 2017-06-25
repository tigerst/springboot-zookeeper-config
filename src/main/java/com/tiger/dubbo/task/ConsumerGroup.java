package com.tiger.dubbo.task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiger.dubbo.callback.ConsumerCallback;
import com.tiger.dubbo.utils.Constants;

public class ConsumerGroup {

	private static final String OUTER_FILE_PREFIX = "file://"; // 外部文件头标记

	private String groupId; // 消费者组

	private String topic; // 消费主题

	protected final ConsumerCallback callback; // 消费者回调接口

	private long timeout = 100; // 取数据时间，默认值100ms

	private ExecutorService exec; // 任务线程池

	private List<ConsumerRunner> taskList = new ArrayList<ConsumerRunner>(); // 任务列表

	private KafkaConsumer<String, String> consumer;

	private Properties props;

	public ConsumerGroup(String groupId, String topic, ConsumerCallback callback, String consumerPropsPath) {
		this.groupId = groupId;
		this.topic = topic;
		this.callback = callback;
		this.props = loadPropsByFilePath(consumerPropsPath);
		validGroupId();
	}

	public ConsumerGroup(String groupId, String topic, ConsumerCallback callback, Properties props) {
		this.groupId = groupId;
		this.topic = topic;
		this.callback = callback;
		this.props = props;
		validGroupId();
	}

	public ConsumerGroup(String topic, ConsumerCallback callback, String consumerPropsPath) {
		this.topic = topic;
		this.callback = callback;
		this.props = loadPropsByFilePath(consumerPropsPath);
		validGroupId();
	}

	public ConsumerGroup(String topic, ConsumerCallback callback, Properties props) {
		this.topic = topic;
		this.callback = callback;
		this.props = props;
		validGroupId();
	}

	/**
	 * 功能：加载消费者属性
	 * 
	 * @param consumerPropsPath
	 * @return
	 */
	private Properties loadPropsByFilePath(String consumerPropsPath) {
		Properties props = null;
		InputStream is = null;
		try {

			if (consumerPropsPath.startsWith(OUTER_FILE_PREFIX))
				is = new FileInputStream(consumerPropsPath.substring(OUTER_FILE_PREFIX.length()));
			else
				is = this.getClass().getResourceAsStream(consumerPropsPath); // 加载项目中配置文件输入流
			props = new Properties();
			props.load(is); // 将输入流中的配置加载进Properties
		} catch (FileNotFoundException e) {
			throw new RuntimeException("消费者属性文件不存在", e);
		} catch (IOException e) {
			throw new RuntimeException("加载消费者属性文件失败", e);
		}
		return props;
	}

	/**
	 * 功能：校验groupId
	 * 
	 * @param props
	 */
	private void validGroupId() {
		String groupId = props.getProperty("group.id");
		if (this.groupId != null && groupId != null) { // 以文件配置为准
			this.groupId = groupId;
		} else if (this.groupId != null && groupId == null) {
			props.put("group.id", this.groupId); // 设置groupId
		}else if(this.groupId == null && groupId != null) { 
			props.put("group.id", groupId); // 设置groupId
		}else {
			throw new RuntimeException("groupId不存在");
		}
	}

	/**
	 * 功能：构建消费者
	 * 
	 * @param groupSize
	 *            group中的消费者数
	 */
	public void buildAndRunConsumers(int groupSize) {
		groupSize = groupSize > 0 ? groupSize : 1;	//小于等于0的设置为1
		exec = Executors.newFixedThreadPool(groupSize);
		ConsumerRunner consumerRunner;
		KafkaConsumer<String, String> consumer;
		for (int i = 1; i <= groupSize; i++) {
			consumer = new KafkaConsumer<String, String>(props);	//创建消费者
			consumerRunner = new ConsumerRunner(consumer, callback);	//根据消费者与回调接口创建任务
			exec.submit(consumerRunner);	//提交任务到线程池中
			taskList.add(consumerRunner);	//加入任务列表中
		}
	}

	/**
	 * 功能：关闭消费者组
	 */
	public void close() {
		if (taskList != null && taskList.size() > 0) {
			for (ConsumerRunner consumerRunner : taskList) {
				consumerRunner.closed.set(true);
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
		exec.shutdown();
	}

	/**
	 * 功能：内部类为消费任务
	 * 
	 * @author Tiger
	 *
	 */
	private class ConsumerRunner implements Runnable {
		
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final AtomicBoolean closed = new AtomicBoolean(false);

		private final KafkaConsumer<String, String> consumer;	//任务线程私有消费者

		private final ConsumerCallback callback; // 消费者回调接口

		public ConsumerRunner(KafkaConsumer<String, String> consumer, ConsumerCallback callback) {
			this.consumer = consumer;
			this.callback = callback;
		}

		@Override
		public void run() {
			consumer.subscribe(Arrays.asList(topic));	//设置订阅主题
			while (!closed.get()) {
				ConsumerRecords<String, String> records = consumer.poll(timeout);
				for (ConsumerRecord<String, String> record : records){	//捕捉每一条消息处理异常，并将消息放入重新消费队列中
					try {
						callback.doAction(record);	//回调接口用于处理单条记录
					} catch (Exception e) {
						Constants.reConsumeMsgQueue.add(record);	//将处理失败消息放入重消费队列中
						logger.error("处理消息异常", e);
					}
				}
			}
		}
	}

	public long getTimeout() {
		return timeout;
	}

	public List<ConsumerRunner> getTaskList() {
		return taskList;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getTopic() {
		return topic;
	}

	public ConsumerCallback getCallback() {
		return callback;
	}

	public KafkaConsumer<String, String> getConsumer() {
		return consumer;
	}
}
