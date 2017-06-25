package com.tiger.dubbo.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component("redisUtil")
public class RedisUtil {

    @SuppressWarnings("rawtypes")
	@Autowired
    @Qualifier("redisTemplate")
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    @SuppressWarnings("unchecked")
	public <K, V> ValueOperations<K, V> setCacheObject(K key, V value) {
        ValueOperations<K, V> operation = redisTemplate.opsForValue();
        operation.set(key, value);
        return operation;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key       缓存键值
     * @param clazz
     * @return 缓存键值对应的数据
     */
    @SuppressWarnings("unchecked")
	public <K, V> V getCacheObject(K key, Class<V> clazz) {
        ValueOperations<K, V> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    @SuppressWarnings("unchecked")
	public <K, V> ListOperations<K, V> setCacheList(K key, List<V> dataList) {
        ListOperations<K, V> listOperation = redisTemplate.opsForList();
        if (null != dataList) {
            int size = dataList.size();
            for (int i = 0; i < size; i++)
                listOperation.rightPush(key, dataList.get(i));
        }
        return listOperation;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     *  @param clazz
     * @return 缓存键值对应的数据
     */
    @SuppressWarnings("unchecked")
	public <K, V> List<V> getCacheList(K key, Class<V> clazz) {
        List<V> dataList = new ArrayList<V>();
        ListOperations<K, V> listOperation = redisTemplate.opsForList();
        Long size = listOperation.size(key);
        for (int i = 0; i < size; i++)
            dataList.add((V) listOperation.leftPop(key));
        return dataList;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    @SuppressWarnings("unchecked")
	public <K, V> BoundSetOperations<K, V> setCacheSet(K key, Set<V> dataSet) {
        BoundSetOperations<K, V> setOperation = redisTemplate.boundSetOps(key);
        Iterator<V> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public <K, V> Set<V> getCacheSet(String key, Class<V> clazz) {
        Set<V> dataSet = new HashSet<V>();
        BoundSetOperations<K, V> operation = redisTemplate.boundSetOps(key);
        Long size = operation.size();
        for (int i = 0; i < size; i++)
            dataSet.add(operation.pop());
        return dataSet;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    @SuppressWarnings("unchecked")
	public <H, HK, HV> HashOperations<H, HK, HV> setCacheMap(H key, Map<HK, HV> dataMap) {
        HashOperations<H, HK, HV> hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) 
            for (Map.Entry<HK, HV> entry : dataMap.entrySet())
                hashOperations.put(key, entry.getKey(), entry.getValue());
        return hashOperations;
    }

    /**
     * 获得缓存的Map
     * @param key
     * @param hkClazz
     * @param hvClazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public <H, HK, HV> Map<HK, HV> getCacheMap(H key, Class<HK> hkClazz, Class<HV> hvClazz) {
        Map<HK, HV> map = redisTemplate.opsForHash().entries(key);
        return map;
    }
    
    /**
     * 获得缓存的Map的key的value对象
     *
     * @param key
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public <H, HK, HV> HV getCacheMap(H key, HK mapKey, Class<HV> hvClazz) {
    	HV mapValue = (HV) redisTemplate.opsForHash().entries(key).get(mapKey);
        return mapValue;
    }
    
    @SuppressWarnings("unchecked")
	public <K> boolean exist(K key) {
		return redisTemplate.hasKey(key);
	}
    
    @SuppressWarnings("unchecked")
	public <K> Set<K> keys(K pattern) {
		return redisTemplate.keys(pattern);
	}
    
    /**
     * 使用redis生成唯一id
     * @param key
     * @param bizPrefix
     * @param bizIdLength
     * @param fillTag
     * @return
     */
    @SuppressWarnings("unchecked")
	public String generatorBizIdByRedis(String key, String bizPrefix, int bizIdLength, String fillTag) {
    	String result = null;
    	String num = redisTemplate.opsForValue().increment(key, 1L) + "";	//把redis中的值自增
    	if(bizPrefix != null){
    		result = bizPrefix;
    		int currentLength = bizPrefix.length() + num.length();
    		int differ  = bizIdLength - currentLength;
    		if(differ > 0){
    			for (int i = 0; i < differ; i++) 
    				result += fillTag;
    			result += num;
    		} else {
    			result += fillTag;
    		}
    	} else {	//默认返回增加后的数据
    		result = num;
    	}
    	return  result;
    }
    
}
