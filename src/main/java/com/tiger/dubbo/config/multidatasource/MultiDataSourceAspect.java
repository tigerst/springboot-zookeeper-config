package com.tiger.dubbo.config.multidatasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

import com.tiger.dubbo.utils.Constants;

@Aspect
@Order(-1)// 调节优先级，保证该AOP在@Transactional之前执行
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)  //自动代理，相当于<aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>
public class MultiDataSourceAspect {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 功能：设置数据源
	 * DataSource注解方法有效，无注解则使用默认数据源（主数据源）。
	 * value为数据源的key，与name相同
	 * manual为true时，需要方法的第一个参数为数据源的key。默认为false。
	 * 
	 * @param point
	 *            方法切点
	 */
	@Before("execution(* com.tiger.*.dao.*.*(..))")
	public void before(JoinPoint point) {
		String methodName = point.getSignature().getName(); // 方法名
		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
		try {
			Method method = point.getTarget().getClass().getMethod(methodName, parameterTypes);
			logger.info("------" + method.getName());
			if (method.isAnnotationPresent(DataSource.class)) { // 指定某个数据源
				DataSource dataSource = method.getAnnotation(DataSource.class);
				String value = dataSource.value(); // 获取注解指定值
				if (dataSource.manual())// 程序人工指定则从方法参数的第一个选取。默认不特殊制定
					value = point.getArgs().length > 0 ? point.getArgs()[0] + "" : Constants.MASTER_DATASOURCE; // 方法参数的第一个值为数据源name
				DynamicDataSourceHolder.putDataSource(value); // 放入动态数据源容器DynamicDataSourceHolder中
			}
		} catch (Exception e) {
			throw new RuntimeException("Dynamic switching data source error", e);
		}
	}
}
