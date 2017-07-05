package com.tiger.dubbo.config.multidatasource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.tiger.dubbo.utils.Constants;

@Configuration
@AutoConfigureBefore(DynamicDataSource.class) // 在动态数据源之前加载
public class DataSourceConfig {

	@Bean("master")
	@Primary
	public DruidDataSource masterDataSource() throws Exception {
		DruidDataSource druidDataSource = new DruidDataSource();
		// druid数据库名
		druidDataSource.setName(Constants.appConfig.getProperty("spring.datasource.master.name"));
		// 数据库url
		druidDataSource.setUrl(Constants.appConfig.getProperty("spring.datasource.master.url"));
		// 数据库用户名
		druidDataSource.setUsername(Constants.appConfig.getProperty("spring.datasource.master.username"));
		// 数据库密码
		druidDataSource.setPassword(Constants.appConfig.getProperty("spring.datasource.master.password"));
		// 数据库驱动器类
		druidDataSource
				.setDriverClassName(Constants.appConfig.getProperty("spring.datasource.master.driverClassName"));

		/*
		 * 配置初始化大小、最小、最大
		 */
		// 初始化大小
		druidDataSource.setInitialSize(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.initialSize"), 5));
		// 线程池最大活动数
		druidDataSource.setMaxActive(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.maxActive"), 10));
		// 线程池最小闲置数
		druidDataSource.setMinIdle(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.minIdle"), 3));

		// 配置获取连接等待超时的时间
		druidDataSource.setMaxWait(
				NumberUtils.toLong(Constants.appConfig.getProperty("spring.datasource.master.maxWait"), 60000));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(NumberUtils.toLong(
				Constants.appConfig.getProperty("spring.datasource.master.timeBetweenEvictionRunsMillis"),
				60000));
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(NumberUtils.toLong(
				Constants.appConfig.getProperty("spring.datasource.master.minEvictableIdleTimeMillis"), 300000));
		// 用来检测连接是否有效的sql，要求是一个查询语句
		druidDataSource
				.setValidationQuery(Constants.appConfig.getProperty("spring.datasource.master.validationQuery"));
		/*
		 * 检测连接
		 */
		druidDataSource.setTestWhileIdle(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testWhileIdle")));
		druidDataSource.setTestOnBorrow(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnBorrow")));
		druidDataSource.setTestOnReturn(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnReturn")));

		// 是否开启PSCache
		druidDataSource.setPoolPreparedStatements(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.poolPreparedStatements")));
		// 指定每个连接上PSCache的大小
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(NumberUtils.toInt(Constants.appConfig
				.getProperty("spring.datasource.master.maxPoolPreparedStatementPerConnectionSize")));
		// 配置监控统计拦截的filters
		druidDataSource.setFilters(Constants.appConfig.getProperty("spring.datasource.master.filters"));
		// 设置慢SQL记录
		druidDataSource.setConnectionProperties(
				Constants.appConfig.getProperty("spring.datasource.master.connectionProperties"));
		// 设置公用监控数据，合并多个DruidDataSource的监控数据
		druidDataSource.setUseGlobalDataSourceStat(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.useGlobalDataSourceStat")));
		druidDataSource.init(); // 初始化
		return druidDataSource;
	}

	@Bean("slave1")
	public DruidDataSource slave1DataSource() throws Exception {
		DruidDataSource druidDataSource = new DruidDataSource();
		// druid数据库名
		druidDataSource.setName(Constants.appConfig.getProperty("spring.datasource.slave1.name"));
		// 数据库url
		druidDataSource.setUrl(Constants.appConfig.getProperty("spring.datasource.slave1.url"));
		// 数据库用户名
		druidDataSource.setUsername(Constants.appConfig.getProperty("spring.datasource.slave1.username"));
		// 数据库密码
		druidDataSource.setPassword(Constants.appConfig.getProperty("spring.datasource.slave1.password"));
		// 数据库驱动器类
		druidDataSource
				.setDriverClassName(Constants.appConfig.getProperty("spring.datasource.slave1.driverClassName"));

		/*
		 * 配置初始化大小、最小、最大
		 */
		// 初始化大小
		druidDataSource.setInitialSize(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.initialSize"), 5));
		// 线程池最大活动数
		druidDataSource.setMaxActive(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.maxActive"), 10));
		// 线程池最小闲置数
		druidDataSource.setMinIdle(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.minIdle"), 3));

		// 配置获取连接等待超时的时间
		druidDataSource.setMaxWait(
				NumberUtils.toLong(Constants.appConfig.getProperty("spring.datasource.master.maxWait"), 60000));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(NumberUtils.toLong(
				Constants.appConfig.getProperty("spring.datasource.master.timeBetweenEvictionRunsMillis"),
				60000));
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(NumberUtils.toLong(
				Constants.appConfig.getProperty("spring.datasource.master.minEvictableIdleTimeMillis"), 300000));
		// 用来检测连接是否有效的sql，要求是一个查询语句
		druidDataSource
				.setValidationQuery(Constants.appConfig.getProperty("spring.datasource.master.validationQuery"));
		/*
		 * 检测连接
		 */
		druidDataSource.setTestWhileIdle(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testWhileIdle")));
		druidDataSource.setTestOnBorrow(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnBorrow")));
		druidDataSource.setTestOnReturn(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnReturn")));

		// 是否开启PSCache
		druidDataSource.setPoolPreparedStatements(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.poolPreparedStatements")));
		// 指定每个连接上PSCache的大小
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(NumberUtils.toInt(Constants.appConfig
				.getProperty("spring.datasource.master.maxPoolPreparedStatementPerConnectionSize")));
		// 配置监控统计拦截的filters
		druidDataSource.setFilters(Constants.appConfig.getProperty("spring.datasource.master.filters"));
		// 设置慢SQL记录
		druidDataSource.setConnectionProperties(
				Constants.appConfig.getProperty("spring.datasource.master.connectionProperties"));
		// 设置公用监控数据，合并多个DruidDataSource的监控数据
		druidDataSource.setUseGlobalDataSourceStat(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.useGlobalDataSourceStat")));
		druidDataSource.init(); // 初始化
		return druidDataSource;
	}

	@Bean("slave2")
	public DruidDataSource slave2DataSource() throws Exception {
		DruidDataSource druidDataSource = new DruidDataSource();
		// druid数据库名
		druidDataSource.setName(Constants.appConfig.getProperty("spring.datasource.slave2.name"));
		// 数据库url
		druidDataSource.setUrl(Constants.appConfig.getProperty("spring.datasource.slave2.url"));
		// 数据库用户名
		druidDataSource.setUsername(Constants.appConfig.getProperty("spring.datasource.slave2.username"));
		// 数据库密码
		druidDataSource.setPassword(Constants.appConfig.getProperty("spring.datasource.slave2.password"));
		// 数据库驱动器类
		druidDataSource
				.setDriverClassName(Constants.appConfig.getProperty("spring.datasource.slave2.driverClassName"));

		/*
		 * 配置初始化大小、最小、最大
		 */
		// 初始化大小
		druidDataSource.setInitialSize(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.initialSize"), 5));
		// 线程池最大活动数
		druidDataSource.setMaxActive(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.maxActive"), 10));
		// 线程池最小闲置数
		druidDataSource.setMinIdle(
				NumberUtils.toInt(Constants.appConfig.getProperty("spring.datasource.master.minIdle"), 3));

		// 配置获取连接等待超时的时间
		druidDataSource.setMaxWait(
				NumberUtils.toLong(Constants.appConfig.getProperty("spring.datasource.master.maxWait"), 60000));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(NumberUtils.toLong(
				Constants.appConfig.getProperty("spring.datasource.master.timeBetweenEvictionRunsMillis"),
				60000));
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(NumberUtils.toLong(
				Constants.appConfig.getProperty("spring.datasource.master.minEvictableIdleTimeMillis"), 300000));
		// 用来检测连接是否有效的sql，要求是一个查询语句
		druidDataSource
				.setValidationQuery(Constants.appConfig.getProperty("spring.datasource.master.validationQuery"));
		/*
		 * 检测连接
		 */
		druidDataSource.setTestWhileIdle(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testWhileIdle")));
		druidDataSource.setTestOnBorrow(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnBorrow")));
		druidDataSource.setTestOnReturn(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnReturn")));

		// 是否开启PSCache
		druidDataSource.setPoolPreparedStatements(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.poolPreparedStatements")));
		// 指定每个连接上PSCache的大小
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(NumberUtils.toInt(Constants.appConfig
				.getProperty("spring.datasource.master.maxPoolPreparedStatementPerConnectionSize")));
		// 配置监控统计拦截的filters
		druidDataSource.setFilters(Constants.appConfig.getProperty("spring.datasource.master.filters"));
		// 设置慢SQL记录
		druidDataSource.setConnectionProperties(
				Constants.appConfig.getProperty("spring.datasource.master.connectionProperties"));
		// 设置公用监控数据，合并多个DruidDataSource的监控数据
		druidDataSource.setUseGlobalDataSourceStat(BooleanUtils
				.toBoolean(Constants.appConfig.getProperty("spring.datasource.master.useGlobalDataSourceStat")));
		druidDataSource.init(); // 初始化
		return druidDataSource;
	}

}
