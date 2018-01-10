package com.sample.mavenTest.cache;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.AdvancedJedisCommands;
import redis.clients.jedis.BasicCommands;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.ClusterCommands;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ScriptingCommands;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.util.Slowlog;

/**
 *自动对池的处理
 *@author yankang
 *@date 2015年2月5日
 */
@Component
public class EnhancerRedis implements FactoryBean<EnhancerRedis>,  MethodInterceptor, JedisCommands,
	MultiKeyCommands, AdvancedJedisCommands, ScriptingCommands,
	BasicCommands, ClusterCommands{
	@Value("${redis.maxTotal}")
	private int maxTotal;
	@Value("${redis.maxWaitMillis}")
	private int maxWaitMillis;
	@Value("${redis.maxIdle}")
	private int maxIdle;
	@Value("${redis.minIdle}")
	private int minIdle;
	@Value("${redis.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;
	@Value("${redis.hostName}")
	private String host;
	@Value("${redis.port}")
	private int port;
	@Value("${redis.password}")
	private String password;
	
	private JedisPool pool = null;
	
	
	public int getMaxTotal() {
		return maxTotal;
	}


	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}


	public int getMaxIdle() {
		return maxIdle;
	}


	public int getMinIdle() {
		return minIdle;
	}


	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}


	public String getHost() {
		return host;
	}


	public int getPort() {
		return port;
	}


	public String getPassword() {
		return password;
	}


	public JedisPool getPool() {
		return pool;
	}


	@PostConstruct
	public void init() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		if(password == null || password.trim().equals("")) {
			pool = new JedisPool(config, host, port);
		} else {
			pool = new JedisPool(config, host, port, Protocol.DEFAULT_TIMEOUT, password);
		}
	}


	@Override
	public EnhancerRedis getObject() throws Exception {
		Enhancer enhancer = new  Enhancer();
		enhancer.setSuperclass(EnhancerRedis.class);
		enhancer.setCallback(this);
		return (EnhancerRedis) enhancer.create();
	}


	@Override
	public Class<EnhancerRedis> getObjectType() {
		return EnhancerRedis.class;
	}


	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Jedis redis = getRedis();
		Object result = null;
		try {
			Method methodRedis = redis.getClass().getMethod(method.getName(), method.getParameterTypes());
			result = methodRedis.invoke(redis, args);
			returnRedis(redis);
		} catch (Exception e) {
			returnBrokenRedis(redis);
		}
		return result;
	}
	
	
	
	/// 
	
	/**
	 * 获取jedis 实例
	 * @return
	 */
	public Jedis getRedis() {
		return pool.getResource();
	}
	
	/**
	 * 返回jedis 实例
	 */
	public void returnRedis(Jedis jed){
		pool.returnResource(jed);
	}
	
	public void returnBrokenRedis(Jedis jed){
		pool.returnBrokenResource(jed);
	}

	
	///  redis 的操作接口， 并不需要实现， 最终将用jedis的实例来操作

	@Override
	public String clusterNodes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterMeet(String ip, int port) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterAddSlots(int... slots) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterDelSlots(int... slots) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterInfo() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> clusterGetKeysInSlot(int slot, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterSetSlotNode(int slot, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterSetSlotMigrating(int slot, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterSetSlotImporting(int slot, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterSetSlotStable(int slot) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterForget(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterFlushSlots() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long clusterKeySlot(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long clusterCountKeysInSlot(int slot) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterSaveConfig() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterReplicate(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> clusterSlaves(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String clusterFailover() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String ping() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String quit() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String flushDB() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long dbSize() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String select(int index) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String flushAll() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String auth(String password) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String save() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String bgsave() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String bgrewriteaof() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long lastsave() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String shutdown() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String info(String section) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String slaveof(String host, int port) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String slaveofNoOne() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long getDB() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String debug(DebugParams params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String configResetStat() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long waitReplicas(int replicas, long timeout) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object eval(String script, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object eval(String script) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object evalsha(String script) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean scriptExists(String sha1) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Boolean> scriptExists(String... sha1) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String scriptLoad(String script) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> configGet(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String configSet(String parameter, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String slowlogReset() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long slowlogLen() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Slowlog> slowlogGet() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Slowlog> slowlogGet(long entries) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long objectRefcount(String string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String objectEncoding(String string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long objectIdletime(String string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long del(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> blpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> brpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> blpop(String... args) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> brpop(String... args) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> keys(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> mget(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String mset(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long msetnx(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String rename(String oldkey, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long renamenx(String oldkey, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String rpoplpush(String srckey, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> sdiff(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> sinter(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long sinterstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long smove(String srckey, String dstkey, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long sort(String key, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> sunion(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long sunionstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String watch(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String unwatch() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zinterstore(String dstkey, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zunionstore(String dstkey, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long publish(String channel, String message) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String randomKey() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<String> scan(int cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<String> scan(String cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long pfcount(String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String set(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String set(String key, String value, String nxxx, String expx,
			long time) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean exists(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long persist(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String type(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long expireAt(String key, long unixTime) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long ttl(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean setbit(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean getbit(String key, long offset) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long setrange(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getSet(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long setnx(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String setex(String key, int seconds, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long decrBy(String key, long integer) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long decr(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long incrBy(String key, long integer) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long incr(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long append(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String substr(String key, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long hsetnx(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String hmset(String key, Map<String, String> hash) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> hmget(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long hincrBy(String key, String field, long value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long hdel(String key, String... field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long hlen(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> hkeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> hvals(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, String> hgetAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long rpush(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long lpush(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long llen(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> lrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String ltrim(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String lindex(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String lset(String key, long index, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long lrem(String key, long count, String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String rpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long sadd(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> smembers(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long srem(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String spop(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long scard(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean sismember(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String srandmember(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long strlen(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zadd(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zrem(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Double zincrby(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zrevrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zcard(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Double zscore(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> sort(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zcount(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zcount(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrangeByScore(String key, double min, double max,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrangeByScore(String key, String min, String max,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
			double min) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min,
			double max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max,
			String min) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min,
			String max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
			double min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max,
			String min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot,
			String value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long lpushx(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long rpushx(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> blpop(String arg) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> brpop(String arg) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long del(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String echo(String string) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long bitcount(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long bitcount(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<String> sscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<Tuple> zscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long pfadd(String key, String... elements) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long pfcount(String key) {
		// TODO Auto-generated method stub
		return 0;
	}
}
