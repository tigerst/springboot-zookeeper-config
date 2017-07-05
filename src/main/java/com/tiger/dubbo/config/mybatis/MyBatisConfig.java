package com.tiger.dubbo.config.mybatis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.tiger.dubbo.utils.Constants;

@Configuration
@EnableTransactionManagement // 加上这个注解，使得支持事务
public class MyBatisConfig implements TransactionManagementConfigurer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	/**
	 * 功能：mybatis文件配置，扫描所有mapper文件，采用的是xml方式
	 * @return
	 */
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactoryBean() {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		//设置datasource
		sqlSessionFactoryBean.setDataSource(dataSource);
		//设置包别名
		sqlSessionFactoryBean.setTypeAliasesPackage(Constants.appConfig.getProperty("spring.mybatis.typeAliasesPackage"));
		
		setMybatisCache(sqlSessionFactoryBean);	//设置mybatis缓存
		
		setPagePlugin(sqlSessionFactoryBean);	//设置分页插件
        
		/*
		 * 添加XML目录。使用xml配置方式，比较灵活且可实现复杂的sql语句，底层可使用generator生成
		 */
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();	//资源文件路径解析器，xml配置时所需
		try {
			String mappingXml = Constants.appConfig.getProperty("spring.mybatis.mappingXml");
			if(StringUtils.isNotEmpty(mappingXml))
				sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mappingXml));	//mapper.xml配置
			return sqlSessionFactoryBean.getObject();
		} catch (Exception e) {
			logger.error("create sqlSessionFactory error", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 功能：mybatis分页插件
	 * @param sqlSessionFactoryBean
	 */
	private void setPagePlugin(SqlSessionFactoryBean sqlSessionFactoryBean) {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
//        properties.setProperty("closeConn", "true");	//查询后关闭连接
        //数据库官方语言
        properties.setProperty("dialect", Constants.appConfig.getProperty("spring.pageHelper.dialect"));
        //RowBounds是否进行count查询 - 默认不查询
        properties.setProperty("rowBoundsWithCount", Constants.appConfig.getProperty("spring.pageHelper.rowBoundsWithCount"));
        //当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
        properties.setProperty("pageSizeZero", Constants.appConfig.getProperty("spring.pageHelper.pageSizeZero"));
        //是否合理化分页
        properties.setProperty("reasonable", Constants.appConfig.getProperty("spring.pageHelper.reasonable"));
        //是否支持接口参数来传递分页参数，默认false
        properties.setProperty("supportMethodsArguments", Constants.appConfig.getProperty("spring.pageHelper.supportMethodsArguments"));
        //offset作为PageNum使用
        properties.setProperty("offsetAsPageNum", Constants.appConfig.getProperty("spring.pageHelper.offsetAsPageNum"));
        //初始化SqlUtil的PARAMS
//        properties.setProperty("params", Constants.ZK_CONF_MAP.get("spring.pageHelper.param"));
        properties.setProperty("params", Constants.appConfig.getProperty("spring.pageHelper.params"));
        //always总是返回PageInfo类型,check检查返回类型是否为PageInfo,none返回Page
        properties.setProperty("returnPageInfo", Constants.appConfig.getProperty("spring.pageHelper.returnPageInfo"));
        pageHelper.setProperties(properties);
        //添加分页插件
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
	}

	/**
	 * 配置mybatis的缓存，延迟加载等等一系列属性
	 * @param sqlSessionFactoryBean
	 */
	private void setMybatisCache(SqlSessionFactoryBean sqlSessionFactoryBean) {
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();	//设置配置spring-mybatis配置
		//全局映射器启用缓存
		configuration.setCacheEnabled(true);
		//查询时，关闭关联对象即时加载以提高性能 
		configuration.setLazyLoadingEnabled(true);
		//对于未知的SQL查询，允许返回不同的结果集以达到通用的效果
		configuration.setMultipleResultSetsEnabled(true);
		//允许使用列标签代替列名
		configuration.setUseColumnLabel(true);
		//不允许使用自定义的主键值(比如由程序生成的UUID 32位编码作为键值)，数据表的PK生成策略将被覆盖
		configuration.setUseGeneratedKeys(false);
		//对于批量更新操作缓存SQL以提高性能 BATCH,SIMPLE
		configuration.setDefaultExecutorType(ExecutorType.BATCH);
		//数据库超过25000秒仍未响应则超时
		configuration.setDefaultStatementTimeout(25000);
		//Allows using RowBounds on nested statements
		configuration.setSafeRowBoundsEnabled(false);
		//Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names aColumn
		configuration.setMapUnderscoreToCamelCase(true);
		//MyBatis uses local cache to prevent circular references and speed up repeated nested queries. By default (SESSION) all queries executed during a session are cached. If localCacheScope=STATEMENT 
        //local session will be used just for statement execution, no data will be shared between two different calls to the same SqlSession
		configuration.setLocalCacheScope(LocalCacheScope.SESSION);
		//Specifies the JDBC type for null values when no specific JDBC type was provided for the parameter. Some drivers require specifying the column JDBC type but others work with generic values 
        //like NULL, VARCHAR or OTHER.
		configuration.setJdbcTypeForNull(JdbcType.OTHER);
		//Specifies which Object's methods trigger a lazy load
		configuration.setLazyLoadTriggerMethods(new HashSet<String>(Arrays.asList("equals,clone,hashCode,toString".split(","))));
		//设置关联对象加载的形态，此处为按需加载字段(加载字段由SQL指 定)，不会加载关联表的所有字段，以提高性能 
		configuration.setAggressiveLazyLoading(false);
		sqlSessionFactoryBean.setConfiguration(configuration);	//设置mybatis配置
	}

	/**
	 * 功能：创建sqlSessionTemplate
	 * @param sqlSessionFactory
	 * @return
	 */
	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	/**
	 * 功能：启用对事务注解的支持
	 */
	@Bean
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}
}