package com.tiger.dubbo.config.multidatasource;

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
		druidDataSource.setName(Constants.appConfig.getProperty("spring.datasource.master.name").trim());
		// 数据库url
		druidDataSource.setUrl(Constants.appConfig.getProperty("spring.datasource.master.url").trim());
		// 数据库用户名
		druidDataSource.setUsername(Constants.appConfig.getProperty("spring.datasource.master.username").trim());
		// 数据库密码
		druidDataSource.setPassword(Constants.appConfig.getProperty("spring.datasource.master.password").trim());
		// 数据库驱动器类
		druidDataSource
				.setDriverClassName(Constants.appConfig.getProperty("spring.datasource.master.driverClassName").trim());

		/*
		 * 配置初始化大小、最小、最大
		 */
		// 初始化大小
		druidDataSource.setInitialSize(
				Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.initialSize").trim()));
		// 线程池最大活动数
		druidDataSource
				.setMaxActive(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.maxActive").trim()));
		// 线程池最小闲置数
		druidDataSource
				.setMinIdle(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.minIdle").trim()));

		// 配置获取连接等待超时的时间
		druidDataSource
				.setMaxWait(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.maxWait").trim()));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(Long
				.parseLong(Constants.appConfig.getProperty("spring.datasource.master.timeBetweenEvictionRunsMillis").trim()));
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(Long
				.parseLong(Constants.appConfig.getProperty("spring.datasource.master.minEvictableIdleTimeMillis").trim()));
		// 用来检测连接是否有效的sql，要求是一个查询语句
		druidDataSource
				.setValidationQuery(Constants.appConfig.getProperty("spring.datasource.master.validationQuery").trim());
		/*
		 * 检测连接
		 */
		druidDataSource.setTestWhileIdle(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testWhileIdle").trim()));
		druidDataSource.setTestOnBorrow(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnBorrow").trim()));
		druidDataSource.setTestOnReturn(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnReturn").trim()));

		// 是否开启PSCache
		druidDataSource.setPoolPreparedStatements(Boolean
				.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.poolPreparedStatements").trim()));
		// 指定每个连接上PSCache的大小
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(Constants.appConfig
				.getProperty("spring.datasource.master.maxPoolPreparedStatementPerConnectionSize").trim()));
		// 配置监控统计拦截的filters
		druidDataSource.setFilters(Constants.appConfig.getProperty("spring.datasource.master.filters").trim());
		// 设置慢SQL记录
		druidDataSource.setConnectionProperties(
				Constants.appConfig.getProperty("spring.datasource.master.connectionProperties").trim());
		// 设置公用监控数据，合并多个DruidDataSource的监控数据
		druidDataSource.setUseGlobalDataSourceStat(Boolean
				.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.useGlobalDataSourceStat").trim()));
		druidDataSource.init(); // 初始化
		return druidDataSource;
	}

	@Bean("slave1")
	public DruidDataSource slave1DataSource() throws Exception {
		DruidDataSource druidDataSource = new DruidDataSource();
		// druid数据库名
		druidDataSource.setName(Constants.appConfig.getProperty("spring.datasource.slave1.name").trim());
		// 数据库url
		druidDataSource.setUrl(Constants.appConfig.getProperty("spring.datasource.slave1.url").trim());
		// 数据库用户名
		druidDataSource.setUsername(Constants.appConfig.getProperty("spring.datasource.slave1.username").trim());
		// 数据库密码
		druidDataSource.setPassword(Constants.appConfig.getProperty("spring.datasource.slave1.password").trim());
		// 数据库驱动器类
		druidDataSource
				.setDriverClassName(Constants.appConfig.getProperty("spring.datasource.slave1.driverClassName").trim());

		/*
		 * 配置初始化大小、最小、最大
		 */
		// 初始化大小
		druidDataSource.setInitialSize(
				Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.initialSize").trim()));
		// 线程池最大活动数
		druidDataSource
				.setMaxActive(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.maxActive").trim()));
		// 线程池最小闲置数
		druidDataSource
				.setMinIdle(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.minIdle").trim()));

		// 配置获取连接等待超时的时间
		druidDataSource
				.setMaxWait(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.maxWait").trim()));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(Long
				.parseLong(Constants.appConfig.getProperty("spring.datasource.master.timeBetweenEvictionRunsMillis").trim()));
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(Long
				.parseLong(Constants.appConfig.getProperty("spring.datasource.master.minEvictableIdleTimeMillis").trim()));
		// 用来检测连接是否有效的sql，要求是一个查询语句
		druidDataSource
				.setValidationQuery(Constants.appConfig.getProperty("spring.datasource.master.validationQuery").trim());
		/*
		 * 检测连接
		 */
		druidDataSource.setTestWhileIdle(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testWhileIdle").trim()));
		druidDataSource.setTestOnBorrow(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnBorrow").trim()));
		druidDataSource.setTestOnReturn(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnReturn").trim()));

		// 是否开启PSCache
		druidDataSource.setPoolPreparedStatements(Boolean
				.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.poolPreparedStatements").trim()));
		// 指定每个连接上PSCache的大小
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(Constants.appConfig
				.getProperty("spring.datasource.master.maxPoolPreparedStatementPerConnectionSize").trim()));
		// 配置监控统计拦截的filters
		druidDataSource.setFilters(Constants.appConfig.getProperty("spring.datasource.master.filters").trim());
		// 设置慢SQL记录
		druidDataSource.setConnectionProperties(
				Constants.appConfig.getProperty("spring.datasource.master.connectionProperties").trim());
		// 设置公用监控数据，合并多个DruidDataSource的监控数据
		druidDataSource.setUseGlobalDataSourceStat(Boolean
				.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.useGlobalDataSourceStat").trim()));
		druidDataSource.init(); // 初始化
		return druidDataSource;
	}

	@Bean("slave2")
	public DruidDataSource slave2DataSource() throws Exception {
		DruidDataSource druidDataSource = new DruidDataSource();
		// druid数据库名
		druidDataSource.setName(Constants.appConfig.getProperty("spring.datasource.slave2.name").trim());
		// 数据库url
		druidDataSource.setUrl(Constants.appConfig.getProperty("spring.datasource.slave2.url").trim());
		// 数据库用户名
		druidDataSource.setUsername(Constants.appConfig.getProperty("spring.datasource.slave2.username").trim());
		// 数据库密码
		druidDataSource.setPassword(Constants.appConfig.getProperty("spring.datasource.slave2.password").trim());
		// 数据库驱动器类
		druidDataSource
				.setDriverClassName(Constants.appConfig.getProperty("spring.datasource.slave2.driverClassName").trim());

		/*
		 * 配置初始化大小、最小、最大
		 */
		// 初始化大小
		druidDataSource.setInitialSize(
				Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.initialSize").trim()));
		// 线程池最大活动数
		druidDataSource
				.setMaxActive(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.maxActive").trim()));
		// 线程池最小闲置数
		druidDataSource
				.setMinIdle(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.minIdle").trim()));

		// 配置获取连接等待超时的时间
		druidDataSource
				.setMaxWait(Integer.parseInt(Constants.appConfig.getProperty("spring.datasource.master.maxWait").trim()));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(Long
				.parseLong(Constants.appConfig.getProperty("spring.datasource.master.timeBetweenEvictionRunsMillis").trim()));
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(Long
				.parseLong(Constants.appConfig.getProperty("spring.datasource.master.minEvictableIdleTimeMillis").trim()));
		// 用来检测连接是否有效的sql，要求是一个查询语句
		druidDataSource
				.setValidationQuery(Constants.appConfig.getProperty("spring.datasource.master.validationQuery").trim());
		/*
		 * 检测连接
		 */
		druidDataSource.setTestWhileIdle(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testWhileIdle").trim()));
		druidDataSource.setTestOnBorrow(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnBorrow").trim()));
		druidDataSource.setTestOnReturn(
				Boolean.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.testOnReturn").trim()));

		// 是否开启PSCache
		druidDataSource.setPoolPreparedStatements(Boolean
				.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.poolPreparedStatements").trim()));
		// 指定每个连接上PSCache的大小
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(Constants.appConfig
				.getProperty("spring.datasource.master.maxPoolPreparedStatementPerConnectionSize").trim()));
		// 配置监控统计拦截的filters
		druidDataSource.setFilters(Constants.appConfig.getProperty("spring.datasource.master.filters").trim());
		// 设置慢SQL记录
		druidDataSource.setConnectionProperties(
				Constants.appConfig.getProperty("spring.datasource.master.connectionProperties").trim());
		// 设置公用监控数据，合并多个DruidDataSource的监控数据
		druidDataSource.setUseGlobalDataSourceStat(Boolean
				.parseBoolean(Constants.appConfig.getProperty("spring.datasource.master.useGlobalDataSourceStat").trim()));
		druidDataSource.init(); // 初始化
		return druidDataSource;
	}

}
