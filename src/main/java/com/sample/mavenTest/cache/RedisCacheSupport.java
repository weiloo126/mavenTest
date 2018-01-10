package com.sample.mavenTest.cache;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Redis操作工具
 * 
 * @author kevin
 *
 */
@Repository("redisCacheSupport")
public class RedisCacheSupport {
	
	final static Logger LOGGER = LoggerFactory.getLogger(RedisCacheSupport.class);
	
	@Autowired
	protected EnhancerRedis redis;
	
	/**
	 * 根据key判断是否存在，存在返回true，反之返回false
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		return redis.exists(key);
	}

	/**
	 * 根据key和field判断在redis中是否存在。存在返回true，反之返回false
	 * @param key	redis的key值
	 * @param field		Map的key值
	 * @return
	 */
	public boolean exists(String key, String field) {
		return redis.hexists(key, field);
	}

	/**
	 * 根据Key获取缓存中的值
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return redis.get(key);
	}

	/**
	 * 设置指定的在Redis的键的字符串值，并返回其原来的值。<br>
	 * 如果键不存在，则返回nil
	 * @param key	缓存的KEY值
	 * @param value	缓存的新值
	 * @return	如果键存在，则返回旧值，反之返回nil
	 */
	public String getSet(String key, String value) {
		return redis.getSet(key, value);
	}

	/**
	 * 设置值到缓存中
	 * @param key	缓存的键
	 * @param value	缓存的值
	 */
	public void put(String key, String value) {
		redis.set(key, value);
	}

	/**
	 * 设置值到Redis缓存的Map集合中
	 * @param key	redis的键
	 * @param field		redis的map键
	 * @param value	Map键对应的值
	 */
	public void put(String key, String field, String value) {
		redis.hset(key, field, value);
	}

	/**
	 * 设置缓存值，同时设置失效时间
	 * @param key	缓存KEY
	 * @param value	缓存值
	 * @param expirySeconds	失效时间，xx秒
	 */
	public void put(String key, String value, int expirySeconds) {
		redis.set(key, value);
		redis.expire(key, expirySeconds);
	}

	/**
	 * 设置key的失效时间
	 * @param key	缓存的key
	 * @param seconds	失效时间，秒
	 * @return	设置成功则返回true，反之返回false
	 */
	public boolean expire(String key, int seconds) {
		long rs = redis.expire(key, seconds);
		return rs == 1 ? true : false;
	}

	/**
	 * 从缓存中删除指定KEY值
	 * @param key
	 * @return	删除成功则返回true，反之返回false
	 */
	public boolean remove(String key) {
		long rs = redis.del(key);
		return rs > 0 ? true : false;
	}

	/**
	 * 从缓存Map中删除指定KEY值
	 * @param key
	 * @param field
	 * @return	删除成功则返回true，反之返回false
	 */
	public boolean remove(String key, String field) {
		long rs = redis.hdel(key, field);
		return rs > 0 ? true : false;
	}
	
	/**
	 * 获取缓存Map对象，包括Map的所有键值
	 * @param key	缓存的Key值
	 * @return	缓存的整个Map对象
	 */
	public Map<String, String> hgetall(String key) {
		return redis.hgetAll(key);
	}

	/**
	 * 根据Key和field获取缓存Map中的值
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key,String field) {
		return redis.hget(key, field);
	}

	/**
	 * 删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略。
	 * @param key	缓存的键
	 * @param fields	缓存Map对象的key值集合
	 * @return
	 */
	public boolean hdel(String key, String... fields) {
		long rs = redis.hdel(key, fields);
		return rs > 0 ? true : false;
	}

	/**
	 * 获取哈希表中的所有字段名
	 * @param key	缓存的KEY值
	 * @return
	 */
	public Set<String> hkeys(String key) {
		return redis.hkeys(key);
	}

	/**
	 * 为哈希表中的字段赋值 。<br>
	 * 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。<br>
	 * 如果字段已经存在于哈希表中，旧值将被覆盖。
	 * @param key	
	 * @param field
	 * @param value
	 * @return	如果字段是哈希表中的一个新建字段，并且值设置成功，返回 1 。<br>
	 * 如果哈希表中域字段已经存在且旧值已被新值覆盖，返回 0 。
	 */
	public long hset(String key, String field, String value) {
		return redis.hset(key, field, value);
	}
	
	/**
	 * 为哈希表中的字段赋值 。<br>
	 * @param key	
	 * @param hash
	 * @return	
	 */
	public String hmset(String key, Map<String, String> hash) {
		return redis.hmset(key, hash);
	}
	
	/**
	 * 将 key 中储存的数字值增一。
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * @param key	
	 * @return	
	 */
	public Long incr(String key) {
		return redis.incr(key);
	}
	
	/**
	 * 将 key 中储存的数字值减一。
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * @param key
	 * @return
	 */
	public Long decr(String key) {
		return redis.decr(key);
	}
	
	
	/**
	 * 入栈
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lPush(String key, String value) {
		return redis.lpush(key, value);
	}

	/**
	 * 出栈（阻塞）
	 * @param time
	 * @param key
	 *            （等待阻塞时间，0为永久）
	 * @return
	 */
	public Object bRPop(Integer time,String key) {
		return redis.brpop(time,key);
	}

	/**
	 * 获取键key到期的剩余时间(秒)
	 * 
	 * @param key
	 * @return 以毫秒为单位的整数值TTL或负值
	 * -1, 如果key没有到期超时
	 * -2, 如果键不存在
	 */
	public Long ttl(String key){
		return redis.ttl(key);
	}
	
	public Set<String> keys(String pattern) {
		return redis.keys(pattern);
	}
}