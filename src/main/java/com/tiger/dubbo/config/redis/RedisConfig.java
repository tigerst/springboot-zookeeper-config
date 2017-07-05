package com.tiger.dubbo.config.redis;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.dubbo.utils.Constants;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching // 启用缓存
public class RedisConfig {

	@Bean("jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大连接数：能够同时建立的“最大链接个数”
		jedisPoolConfig.setMaxTotal(NumberUtils.toInt(Constants.appConfig.getProperty("redis.pool.maxTotal").trim(), 10));
		// 最大空闲数：空闲链接数大于maxIdle时，将进行回收
		jedisPoolConfig.setMaxIdle(NumberUtils.toInt(Constants.appConfig.getProperty("redis.pool.maxIdle").trim(), 5));
		// 最小空闲数：低于minIdle时，将创建新的链接
		jedisPoolConfig.setMinIdle(NumberUtils.toInt(Constants.appConfig.getProperty("redis.pool.minIdle").trim(), 2));
		// 最大等待时间：单位ms
		jedisPoolConfig.setMaxWaitMillis(NumberUtils.toLong(Constants.appConfig.getProperty("redis.pool.maxWait").trim(), 1000));
		// 使用连接时，检测连接是否成功
		jedisPoolConfig
				.setTestOnBorrow(BooleanUtils.toBoolean(Constants.appConfig.getProperty("redis.pool.testOnBorrow").trim()));
		// 返回连接时，检测连接是否成功
		jedisPoolConfig
				.setTestOnReturn(BooleanUtils.toBoolean(Constants.appConfig.getProperty("redis.pool.testOnReturn").trim()));
		return jedisPoolConfig;
	}

	@Bean("jedisConnectionFactory")
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		// 服务主机
		jedisConnectionFactory.setHostName(Constants.appConfig.getProperty("redis.hostName").trim());
		// 服务端口
		jedisConnectionFactory.setPort(NumberUtils.toInt(Constants.appConfig.getProperty("redis.port").trim(),6379));
		// 设置redis
		jedisConnectionFactory.setDatabase(NumberUtils.toInt(Constants.appConfig.getProperty("redis.dbIndex").trim()));
		
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		jedisConnectionFactory.afterPropertiesSet(); // 初始化
		return jedisConnectionFactory;
	}

	@SuppressWarnings("unchecked")
	@Bean("redisTemplate")
	public <K, V> RedisTemplate<K, V> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<K, V> redisTemplate = new RedisTemplate<K, V>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		Jackson2JsonRedisSerializer<V> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<V>(
				(Class<V>) Object.class);
		ObjectMapper om = new ObjectMapper();
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet(); // 初始化
		return redisTemplate;
	}

}
