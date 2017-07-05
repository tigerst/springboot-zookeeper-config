package com.tiger.dubbo.config.multidatasource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.ReflectionUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.tiger.dubbo.config.mybatis.MyBatisConfig;
import com.tiger.dubbo.utils.Constants;

/**
 * 功能：获取动态数据源
 * 配置时，targetDataSources必须配置一个名为master的数据源，不能配置名为replica-set-slave-999999-
 * host的数据源
 * 
 * @author Tiger
 *
 */
@Configuration("dataSource")
@AutoConfigureBefore(MyBatisConfig.class) // 在动态数据源之前加载
public class DynamicDataSource extends AbstractRoutingDataSource {

	private Integer slaveCount; // 从库数量

	private static final int COUNTER_UPPER_BOUND = 9999; // 计数器最大边界

	// 轮询计数,初始为-1,AtomicInteger是线程安全的
	private AtomicInteger counter = new AtomicInteger(-1);

	// 记录读库的name
	private List<Object> slaveDataSourceList = new ArrayList<Object>(2);

	@Autowired
	@Qualifier("master")
	private DruidDataSource masterDataSource;

	@Autowired
	@Qualifier("slave1")
	private DruidDataSource slave1DataSource;

	@Autowired
	@Qualifier("slave2")
	private DruidDataSource slave2DataSource;

	@Override
	protected Object determineCurrentLookupKey() {
		// 使用DynamicDataSourceHolder保证线程安全，并且得到当前线程中的数据源key
		Object key = DynamicDataSourceHolder.getDataSouce();
		if (Constants.SLAVE_DATASOURCE_CODE.equals(key)) // 从机代号，则在从机集群中选出一个
			key = getSlaveDataSourceKey();
		logger.info("----datasource-key----" + key);
		return key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() {
		try {
			setMultiDataSource();
		} catch (Exception e) {
			throw new IllegalArgumentException("Property 'targetDataSources' is required");
		}
		super.afterPropertiesSet(); // 调用父类初始化方法，初始化属性
		// 由于父类的resolvedDataSources属性是私有的子类获取不到，需要使用反射获取
		Field field = ReflectionUtils.findField(AbstractRoutingDataSource.class, "resolvedDataSources");
		field.setAccessible(true); // 设置可访问
		try {
			Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) field.get(this);
			this.slaveCount = resolvedDataSources.size() - 1; // 读库的数据量等于数据源总数减去写库的数量
			boolean hasMaster = false;
			for (Entry<Object, DataSource> entry : resolvedDataSources.entrySet()) {
				if (Constants.MASTER_DATASOURCE.equals(entry.getKey())) {
					hasMaster = true; // 表明主机存在
					continue;
				}
				slaveDataSourceList.add(entry.getKey());// 非主机加入从机集合
			}
			if (!hasMaster)
				throw new Exception("No master found");
		} catch (Exception e) {
			throw new IllegalArgumentException("Init dataSource error, please check your configuration", e);
		}
	}

	/**
	 * 功能：设置多数据源
	 * 
	 * @throws Exception
	 */
	private Map<Object, Object> setMultiDataSource() throws Exception {
		Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
		dataSourceMap.put(masterDataSource.getName(), masterDataSource);
		dataSourceMap.put(slave1DataSource.getName(), slave1DataSource);
		dataSourceMap.put(slave2DataSource.getName(), slave2DataSource);
		super.setTargetDataSources(dataSourceMap);
		super.setDefaultTargetDataSource(masterDataSource); // 设置默认数据库为主库
		return dataSourceMap;
	}

	/**
	 * 功能：轮询（或随机）选出从机
	 * 
	 * @return
	 */
	public Object getSlaveDataSourceKey() {
		Integer index = counter.incrementAndGet() % slaveCount; // 得到的下标为：0、1、2、3……
		if (counter.get() >= COUNTER_UPPER_BOUND) // 超过计数器最大值，则将计数器重置
			counter.set(-1); // 还原默认值
		return slaveDataSourceList.get(index);
	}

}
