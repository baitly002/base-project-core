package org.basecode.core.config.base;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnClass(RedisConnectionFactory.class)
public class RedisClientTemplate {

	@Autowired
	StringRedisTemplate redisTemplate;
	
	
	public RedisClientTemplate(RedisConnectionFactory connectionFactory) {
		redisTemplate = new StringRedisTemplate(connectionFactory);
		redisTemplate.afterPropertiesSet();
		init();
	}
	
	

	public RedisClientTemplate() {
	}

	private ValueOperations<String, String> opsForValue = null;

	private HashOperations<String, String, String> opsForHash = null;

	private ListOperations<String, String> opsForList = null;

	private SetOperations<String, String> opsForSet = null;

	private ZSetOperations<String, String> opsForZSet = null;

	@PostConstruct
	private void init() {
		opsForValue = redisTemplate.opsForValue();
		opsForHash = redisTemplate.opsForHash();
		opsForList = redisTemplate.opsForList();
		opsForSet = redisTemplate.opsForSet();
		opsForZSet = redisTemplate.opsForZSet();
	}

	public ValueOperations<String, String> getOpsForVakue() {
		return opsForValue;
	}

	public ListOperations<String, String> getOpsForList() {
		return opsForList;
	}

	public HashOperations<String, String, String> getOpsForHash() {
		return opsForHash;
	}

	public SetOperations<String, String> getOpsForSet() {
		return opsForSet;
	}

	public ZSetOperations<String, String> getOpsForZSet() {
		return opsForZSet;
	}

	public String json(Object obj) {
		return JSON.toJSONString(obj);
	}

	public <T> T conversion(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);

	}

	/**
	 * 设置值，并标上到指定时间过期
	 * 
	 * @param key
	 * @param value
	 * @param unix_timestamp
	 * @return
	 */
	public void setexat(String key, Object value, int unix_timestamp) {
		set(key, value);
		expire(key, unix_timestamp, TimeUnit.SECONDS);
	}

	public void setexat(String key, Object value, long timestamp) {
		int unix_timestamp = new Long(timestamp / 1000).intValue();
		setexat(key, value, unix_timestamp);
	}

	public void setexat(String key, Object value, Date date) {
		set(key, value);
		expireAt(key, date);
	}

	/**
	 * 删除key
	 * 
	 * @param key
	 */
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 批量删除key
	 * 
	 * @param keys
	 */
	public void delete(Collection<String> keys) {
		redisTemplate.delete(keys);
	}

	/**
	 * 序列化key
	 * 
	 * @param key
	 * @return
	 */
	public byte[] dump(String key) {
		return redisTemplate.dump(key);
	}

	/**
	 * 是否存在key
	 * 
	 * @param key
	 * @return
	 */
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}
	
	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public Boolean expire(String key, long timeout, TimeUnit unit) {
		return redisTemplate.expire(key, timeout, unit);
	}
	/**
	 * 设置过期时间(秒)
	 * 
	 * @param key
	 * @param timeout
	 * @return
	 */
	public Boolean expire(String key, long timeout) {
		return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}


	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param date
	 * @return
	 */
	public Boolean expireAt(String key, Date date) {
		return redisTemplate.expireAt(key, date);
	}

	public Boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 查找匹配的key
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	/**
	 * 将当前数据库的 key 移动到给定的数据库 db 当中
	 * 
	 * @param key
	 * @param dbIndex
	 * @return
	 */
	public Boolean move(String key, int dbIndex) {
		return redisTemplate.move(key, dbIndex);
	}

	/**
	 * 移除 key 的过期时间，key 将持久保持
	 * 
	 * @param key
	 * @return
	 */
	public Boolean persist(String key) {
		return redisTemplate.persist(key);
	}

	/**
	 * 返回 key 的剩余的过期时间
	 * 
	 * @param key
	 * @param unit
	 * @return
	 */
	public Long getExpire(String key, TimeUnit unit) {
		return redisTemplate.getExpire(key, unit);
	}

	/**
	 * 返回 key 的剩余的过期时间
	 * 
	 * @param key
	 * @param unit
	 * @return
	 */
	public Long ttl(String key, TimeUnit unit) {
		return redisTemplate.getExpire(key, unit);
	}

	/**
	 * 返回 key 的剩余的过期时间
	 * 
	 * @param key
	 * @return
	 */
	public Long getExpire(String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * 从当前数据库中随机返回一个 key
	 * 
	 * @return
	 */
	public String randomKey() {
		return redisTemplate.randomKey();
	}

	/**
	 * 修改 key 的名称
	 * 
	 * @param oldKey
	 * @param newKey
	 */
	public void rename(String oldKey, String newKey) {
		redisTemplate.rename(oldKey, newKey);
	}

	/**
	 * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
	 * 
	 * @param oldKey
	 * @param newKey
	 * @return
	 */
	public Boolean renameIfAbsent(String oldKey, String newKey) {
		return redisTemplate.renameIfAbsent(oldKey, newKey);
	}

	/**
	 * 返回 key 所储存的值的类型
	 * 
	 * @param key
	 * @return
	 */
	public DataType type(String key) {
		return redisTemplate.type(key);
	}

	/** -------------------string相关操作--------------------- */

	/**
	 * 设置指定 key 的值
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {

		opsForValue.set(key, value);
	}

	/**
	 * 设置指定 key 的值
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {

		opsForValue.set(key, JSON.toJSONString(value));
	}

	/**
	 * 获取指定 key 的值
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return opsForValue.get(key);
	}

	/**
	 * 获取指定 key 的值
	 * 
	 * @param key
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz) {
		return conversion(opsForValue.get(key), clazz);
	}

	public Long getLong(String key) {
		return Long.valueOf(get(key));
	}

	/**
	 * 返回 key 中字符串值的子字符
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public String getRange(String key, long start, long end) {
		return opsForValue.get(key, start, end);
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String getAndSet(String key, String value) {
		return opsForValue.getAndSet(key, value);
	}

	/**
	 * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
	 * 
	 * @param key
	 * @param offset
	 * @return
	 */
	public Boolean getBit(String key, long offset) {
		return opsForValue.getBit(key, offset);
	}

	/**
	 * 批量获取
	 * 
	 * @param keys
	 * @return
	 */
	public List<String> multiGet(Collection<String> keys) {
		return opsForValue.multiGet(keys);
	}

	/**
	 * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
	 * 
	 * @param key
	 * @param offset 位置
	 * @param value   值,true为1, false为0
	 * @return
	 */
	public boolean setBit(String key, long offset, boolean value) {
		return opsForValue.setBit(key, offset, value);
	}

	/**
	 * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
	 * 
	 * @param key
	 * @param value
	 * @param timeout 过期时间
	 * @param unit    时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
	 *                秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
	 */
	public void setEx(String key, String value, long timeout, TimeUnit unit) {
		opsForValue.set(key, value, timeout, unit);
	}

	/**
	 * 只有在 key 不存在时设置 key 的值
	 * 
	 * @param key
	 * @param value
	 * @return 之前已经存在返回false,不存在返回true
	 */
	public boolean setIfAbsent(String key, String value) {
		return opsForValue.setIfAbsent(key, value);
	}

	/**
	 * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
	 * 
	 * @param key
	 * @param value
	 * @param offset 从指定位置开始覆写
	 */
	public void setRange(String key, String value, long offset) {
		opsForValue.set(key, value, offset);
	}

	/**
	 * 获取字符串的长度
	 * 
	 * @param key
	 * @return
	 */
	public Long size(String key) {
		return opsForValue.size(key);
	}

	/**
	 * 批量添加
	 * 
	 * @param maps
	 */
	public void multiSet(Map<String, String> maps) {
		opsForValue.multiSet(maps);
	}

	/**
	 * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
	 * 
	 * @param maps
	 * @return 之前已经存在返回false,不存在返回true
	 */
	public boolean multiSetIfAbsent(Map<String, String> maps) {
		return opsForValue.multiSetIfAbsent(maps);
	}

	/**
	 * 增加(自增长), 负数则为自减
	 * 
	 * @param key
	 * @param increment
	 * @return
	 */
	public Long incrBy(String key, long increment) {

		return opsForValue.increment(key, increment);
	}

	public Long incr(String key) {
		return opsForValue.increment(key);
	}

	/**
	 * 
	 * @param key
	 * @param increment
	 * @return
	 */
	public Double incrByFloat(String key, double increment) {
		return opsForValue.increment(key, increment);
	}

	/**
	 * 追加到末尾
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Integer append(String key, String value) {
		return opsForValue.append(key, value);
	}

	/** -------------------hash相关操作------------------------- */

	/**
	 * 获取存储在哈希表中指定字段的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hGet(String key, String field) {
		return opsForHash.get(key, field);
	}

	/**
	 * 获取存储在哈希表中指定字段的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public <T> T hGet(String key, String field, Class<T> clz) {
		return JSON.parseObject(opsForHash.get(key, field), clz);
		//return U.readJson(opsForHash.get(key, field), clz);
	}

	/**
	 * 获取所有给定字段的值
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hGetAll(String key) {
		return opsForHash.entries(key);
	}
	/**
	 * 获取所有给定字段的值
	 * @param <T>
	 * 
	 * @param key
	 * @return
	 */
	public <T> Map<String, T> hGetAll(String key, Class<T> clz) {
		HashOperations<String, String, T> _opsForHash= redisTemplate.opsForHash();
		return _opsForHash.entries(key);
	}

	/**
	 * 获取所有给定字段的值
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public List<String> hMultiGet(String key, Collection<String> fields) {
		return opsForHash.multiGet(key, fields);
	}

	public void hSet(String key, String hashKey, Object obj) {
		opsForHash.put(key, hashKey, JSON.toJSONString(obj));
	}
	
	public void hSet(String key, String hashKey, String value) {
		opsForHash.put(key, hashKey, value);
	}
	
	public void hPut(String key, String hashKey, Object value) {
		opsForHash.put(key, hashKey, JSON.toJSONString(value));
		//opsForHash.put(key, hashKey, U.json(value));
	}

	public void hPutAll(String key, Map<String, String> maps) {
		opsForHash.putAll(key, maps);
	}

	/**
	 * 仅当hashKey不存在时才设置
	 * 
	 * @param key
	 * @param hashKey
	 * @param value
	 * @return
	 */
	public Boolean hPutIfAbsent(String key, String hashKey, String value) {
		return opsForHash.putIfAbsent(key, hashKey, value);
	}

	/**
	 * 删除一个或多个哈希表字段
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public Long hDelete(String key, Object... fields) {
		return opsForHash.delete(key, fields);
	}

	/**
	 * 查看哈希表 key 中，指定的字段是否存在
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hExists(String key, String field) {
		return opsForHash.hasKey(key, field);
	}

	/**
	 * 为哈希表 key 中的指定字段的整数值加上增量 increment
	 * 
	 * @param key
	 * @param field
	 * @param increment
	 * @return
	 */
	public Long hIncrBy(String key, String field, long increment) {
		return opsForHash.increment(key, field, increment);
	}

	/**
	 * 为哈希表 key 中的指定字段的整数值加上增量 increment
	 * 
	 * @param key
	 * @param field
	 * @param delta
	 * @return
	 */
	public Double hIncrByFloat(String key, String field, double delta) {
		return opsForHash.increment(key, field, delta);
	}

	/**
	 * 获取所有哈希表中的字段
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> hKeys(String key) {
		return opsForHash.keys(key);
	}

	/**
	 * 获取哈希表中字段的数量
	 * 
	 * @param key
	 * @return
	 */
	public Long hSize(String key) {
		return opsForHash.size(key);
	}

	/**
	 * 获取哈希表中所有值
	 * 
	 * @param key
	 * @return
	 */
	public List<String> hValues(String key) {
		return opsForHash.values(key);
	}

	/**
	 * 迭代哈希表中的键值对
	 * 
	 * @param key
	 * @param options
	 * @return
	 */
	public Cursor<Entry<String, String>> hScan(String key, ScanOptions options) {
		return opsForHash.scan(key, options);
	}

	/** ------------------------list相关操作---------------------------- */

	/**
	 * 通过索引获取列表中的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public String lIndex(String key, long index) {
		return opsForList.index(key, index);
	}

	/**
	 * 通过索引获取列表中的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public <T> T lIndex(String key, long index, Class<T> clz) {
		String json = opsForList.index(key, index);
		return JSON.parseObject(json, clz);
		//return U.readJson(json, clz);
	}

	public List<String> lall(Long key) {
		return lrange(String.valueOf(key), 0, -1);
	}

	public List<String> lall(String key) {
		return lrange(String.valueOf(key), 0, -1);
	}

	
	/**
	 * 获取列表指定范围内的元素
	 * 
	 * @param key
	 * @param start 开始位置, 0是开始位置
	 * @param end   结束位置, -1返回所有
	 * @return
	 */
	public List<String> lrange(String key, long start, long end) {
		return opsForList.range(key, start, end);
	}

	/**
	 * 获取列表指定范围内的元素
	 * 
	 * @param key
	 * @param start 开始位置, 0是开始位置
	 * @param end   结束位置, -1返回所有
	 * @return
	 */
	public <T> List<T> lRange(String key, long start, long end, Class<T> clz) {
		List<String> ls = opsForList.range(key, 0, -1);
		List<T> resultList = new ArrayList<>();
		ls.forEach(str -> resultList.add(JSON.parseObject(str, clz)));
		//ls.forEach(str -> resultList.add(U.readJson(str, clz)));
		return resultList;
	}

	/**
	 * 获取列表所有元素
	 * 
	 * @param key
	 * @return
	 */
	public <T> List<T> lAll(String key, Class<T> clz) {
		List<String> ls = opsForList.range(key, 0, -1);
		List<T> resultList = new ArrayList<>();
		ls.forEach(str -> resultList.add(jsonToObject(str, clz)));
		return resultList;
	}
	
	
	public <T> T  jsonToObject(String key ,Class<T> clz){
		String string = opsForValue.get(key);
		return JSON.parseObject(string, clz);
		//return U.readJson(string, clz);
	}

	/**
	 * 获取列表所有元素
	 * 
	 * @param key
	 * @return
	 */
	public List<String> lAll(String key) {
		return opsForList.range(key, 0, -1);
	}

	/**
	 * 获取列表所有元素
	 * 
	 * @param key
	 * @return
	 */
	public <T> List<T> lAllLink(String key, Class<T> clz) {
		List<String> ls = opsForList.range(key, 0, -1);
		List<T> resultList = new ArrayList<>();
		ls.forEach(str -> resultList.add(get(str, clz)));
		return resultList;
	}

	/**
	 * 存储在list头部
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lLeftPush(String key, String value) {
		return opsForList.leftPush(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lLeftPushAll(String key, String... value) {
		return opsForList.leftPushAll(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lLeftPushAll(String key, Collection<String> value) {
		return opsForList.leftPushAll(key, value);
	}

	/**
	 * 当list存在的时候才加入
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lLeftPushIfPresent(String key, String value) {
		return opsForList.leftPushIfPresent(key, value);
	}

	/**
	 * 如果pivot存在,再pivot前面添加
	 * 
	 * @param key
	 * @param pivot
	 * @param value
	 * @return
	 */
	public Long lLeftPush(String key, String pivot, String value) {
		return opsForList.leftPush(key, pivot, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lRightPush(String key, String value) {
		return opsForList.rightPush(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> Long lRightPush(String key, T value) {
		return opsForList.rightPush(key, JSON.toJSONString(value));
		//return opsForList.rightPush(key, U.json(value));
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lRightPushAll(String key, String... value) {
		return opsForList.rightPushAll(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lRightPushAll(String key, Collection<String> value) {
		return opsForList.rightPushAll(key, value);
	}

	/**
	 * 为已存在的列表添加值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lRightPushIfPresent(String key, String value) {
		return opsForList.rightPushIfPresent(key, value);
	}

	/**
	 * 在pivot元素的右边添加值
	 * 
	 * @param key
	 * @param pivot
	 * @param value
	 * @return
	 */
	public Long lRightPush(String key, String pivot, String value) {
		return opsForList.rightPush(key, pivot, value);
	}

	/**
	 * 通过索引设置列表元素的值
	 * 
	 * @param key
	 * @param index 位置
	 * @param value
	 */
	public void lSet(String key, long index, String value) {
		opsForList.set(key, index, value);
	}

	/**
	 * 移出并获取列表的第一个元素
	 * 
	 * @param key
	 * @return 删除的元素
	 */
	public String lLeftPop(String key) {
		return opsForList.leftPop(key);
	}

	/**
	 * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 * 
	 * @param key
	 * @param timeout 等待时间
	 * @param unit    时间单位
	 * @return
	 */
	public String lBLeftPop(String key, long timeout, TimeUnit unit) {
		return opsForList.leftPop(key, timeout, unit);
	}

	/**
	 * 移除并获取列表最后一个元素
	 * 
	 * @param key
	 * @return 删除的元素
	 */
	public String lRightPop(String key) {
		return opsForList.rightPop(key);
	}

	/**
	 * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 * 
	 * @param key
	 * @param timeout 等待时间
	 * @param unit    时间单位
	 * @return
	 */
	public String lBRightPop(String key, long timeout, TimeUnit unit) {
		return opsForList.rightPop(key, timeout, unit);
	}

	/**
	 * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
	 * 
	 * @param sourceKey
	 * @param destinationKey
	 * @return
	 */
	public String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
		return opsForList.rightPopAndLeftPush(sourceKey, destinationKey);
	}

	/**
	 * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 * 
	 * @param sourceKey
	 * @param destinationKey
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public String lBRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
		return opsForList.rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
	}

	/**
	 * 删除集合中值等于value得元素
	 * 
	 * @param key
	 * @param index index=0, 删除所有值等于value的元素; index>0, 从头部开始删除第一个值等于value的元素;
	 *              index<0, 从尾部开始删除第一个值等于value的元素;
	 * @param value
	 * @return
	 */
	public Long lRemove(String key, long index, String value) {
		return opsForList.remove(key, index, value);
	}

	/**
	 * 裁剪list
	 * 
	 * @param key
	 * @param start
	 * @param end
	 */
	public void lTrim(String key, long start, long end) {
		opsForList.trim(key, start, end);
	}

	/**
	 * 获取列表长度
	 * 
	 * @param key
	 * @return
	 */
	public Long lLen(String key) {
		return opsForList.size(key);
	}

	/** --------------------set相关操作-------------------------- */

	/**
	 * set添加元素
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Long sAdd(String key, String... values) {
		return opsForSet.add(key, values);
	}

	/**
	 * set移除元素
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Long sRemove(String key, Object... values) {
		return opsForSet.remove(key, values);
	}

	/**
	 * 移除并返回集合的一个随机元素
	 * 
	 * @param key
	 * @return
	 */
	public String sPop(String key) {
		return opsForSet.pop(key);
	}

	/**
	 * 将元素value从一个集合移到另一个集合
	 * 
	 * @param key
	 * @param value
	 * @param destKey
	 * @return
	 */
	public Boolean sMove(String key, String value, String destKey) {
		return opsForSet.move(key, value, destKey);
	}

	/**
	 * 获取集合的大小
	 * 
	 * @param key
	 * @return
	 */
	public Long sSize(String key) {
		return opsForSet.size(key);
	}

	/**
	 * 判断集合是否包含value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Boolean sIsMember(String key, String value) {
		return opsForSet.isMember(key, value);
	}

	/**
	 * 获取两个集合的交集
	 * 
	 * @param key
	 * @param otherKey
	 * @return
	 */
	public Set<String> sIntersect(String key, String otherKey) {
		return opsForSet.intersect(key, otherKey);
	}

	/**
	 * 获取key集合与多个集合的交集
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> sIntersect(String key, Collection<String> otherKeys) {
		return opsForSet.intersect(key, otherKeys);
	}

	/**
	 * key集合与otherKey集合的交集存储到destKey集合中
	 * 
	 * @param key
	 * @param otherKey
	 * @param destKey
	 * @return
	 */
	public Long sIntersectAndStore(String key, String otherKey, String destKey) {
		return opsForSet.intersectAndStore(key, otherKey, destKey);
	}

	/**
	 * key集合与多个集合的交集存储到destKey集合中
	 * 
	 * @param key
	 * @param otherKeys
	 * @param destKey
	 * @return
	 */
	public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		return opsForSet.intersectAndStore(key, otherKeys, destKey);
	}

	/**
	 * 获取两个集合的并集
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> sUnion(String key, String otherKeys) {
		return opsForSet.union(key, otherKeys);
	}

	/**
	 * 获取key集合与多个集合的并集
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> sUnion(String key, Collection<String> otherKeys) {
		return opsForSet.union(key, otherKeys);
	}

	/**
	 * key集合与otherKey集合的并集存储到destKey中
	 * 
	 * @param key
	 * @param otherKey
	 * @param destKey
	 * @return
	 */
	public Long sUnionAndStore(String key, String otherKey, String destKey) {
		return opsForSet.unionAndStore(key, otherKey, destKey);
	}

	/**
	 * key集合与多个集合的并集存储到destKey中
	 * 
	 * @param key
	 * @param otherKeys
	 * @param destKey
	 * @return
	 */
	public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
		return opsForSet.unionAndStore(key, otherKeys, destKey);
	}

	/**
	 * 获取两个集合的差集
	 * 
	 * @param key
	 * @param otherKey
	 * @return
	 */
	public Set<String> sDifference(String key, String otherKey) {
		return opsForSet.difference(key, otherKey);
	}

	/**
	 * 获取key集合与多个集合的差集
	 * 
	 * @param key
	 * @param otherKeys
	 * @return
	 */
	public Set<String> sDifference(String key, Collection<String> otherKeys) {
		return opsForSet.difference(key, otherKeys);
	}

	/**
	 * key集合与otherKey集合的差集存储到destKey中
	 * 
	 * @param key
	 * @param otherKey
	 * @param destKey
	 * @return
	 */
	public Long sDifference(String key, String otherKey, String destKey) {
		return opsForSet.differenceAndStore(key, otherKey, destKey);
	}

	/**
	 * key集合与多个集合的差集存储到destKey中
	 * 
	 * @param key
	 * @param otherKeys
	 * @param destKey
	 * @return
	 */
	public Long sDifference(String key, Collection<String> otherKeys, String destKey) {
		return opsForSet.differenceAndStore(key, otherKeys, destKey);
	}

	public Set<Long> slongMembers(String key) {
		Set<String> lsStr = smembers(key);
		Set<Long> driverIds = new HashSet<>();
		for (String str : lsStr) {
			driverIds.add(Long.valueOf(str));
		}
		return driverIds;
	}
	
	/**
	 * 获取集合所有元素
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> smembers(String key) {
		
		return opsForSet.members(key);
	}

	/**
	 * 随机获取集合中的一个元素
	 * 
	 * @param key
	 * @return
	 */
	public String sRandomMember(String key) {
		return opsForSet.randomMember(key);
	}

	/**
	 * 随机获取集合中count个元素
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public List<String> sRandomMembers(String key, long count) {
		return opsForSet.randomMembers(key, count);
	}

	/**
	 * 随机获取集合中count个元素并且去除重复的
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public Set<String> sDistinctRandomMembers(String key, long count) {
		return opsForSet.distinctRandomMembers(key, count);
	}

	/**
	 * 
	 * @param key
	 * @param options
	 * @return
	 */
	public Cursor<String> sScan(String key, ScanOptions options) {
		return opsForSet.scan(key, options);
	}

	/** ------------------zSet相关操作-------------------------------- */


	public Boolean zAdd(String key, double score, Long member) {
		return  zAdd(key, String.valueOf(member),score);
	}
	
	public Boolean zAdd(String key, double score, String member) {
		return  zAdd(key, String.valueOf(member),score);
	}

	
	/**
	 * 添加元素,有序集合是按照元素的score值由小到大排列
	 * 
	 * @param key
	 * @param value
	 * @param score
	 * @return
	 */
	public Boolean zAdd(String key, String value, double score) {
		return opsForZSet.add(key, value, score);
	}

	/**
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Long zAdd(String key, Set<TypedTuple<String>> values) {
		return opsForZSet.add(key, values);
	}

	/**
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Long zrem(String key, Object... values) {
		return opsForZSet.remove(key, values);
	}

	/**
	 * 增加元素的score值，并返回增加后的值
	 * 
	 * @param key
	 * @param value
	 * @param delta
	 * @return
	 */
	public Double zIncrementScore(String key, String value, double delta) {
		return opsForZSet.incrementScore(key, value.toString(), delta);
	}

	/**
	 * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
	 * 
	 * @param key
	 * @param value
	 * @return 0表示第一位
	 */
	public Long zRank(String key, String value) {
		return opsForZSet.rank(key, value);
	}

	/**
	 * 返回元素在集合的排名,按元素的score值由大到小排列
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long zReverseRank(String key, String value) {
		return opsForZSet.reverseRank(key, value);
	}

	/**
	 * 获取集合的元素, 从小到大排序
	 * 
	 * @param key
	 * @param start 开始位置
	 * @param end   结束位置, -1查询所有
	 * @return
	 */
	public Set<String> zRange(String key, long start, long end) {
		return opsForZSet.range(key, start, end);
	}

	/**
	 * 获取集合元素, 并且把score值也获取
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
		return opsForZSet.rangeWithScores(key, start, end);
	}


	
	

	/**
	 * 根据Score值查询集合元素, 从小到大排序
	 * 
	 * @param key
	 * @param min 最小值
	 * @param max 最大值
	 * @return
	 */
	public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max) {
		return opsForZSet.rangeByScoreWithScores(key, min, max);
	}

	/**
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
		return opsForZSet.rangeByScoreWithScores(key, min, max, start, end);
	}

	/**
	 * 获取集合的元素, 从大到小排序
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zReverseRange(String key, long start, long end) {
		return opsForZSet.reverseRange(key, start, end);
	}

	/**
	 * 获取集合的元素, 从大到小排序, 并返回score值
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
		return opsForZSet.reverseRangeWithScores(key, start, end);
	}

	/**
	 * 根据Score值查询集合元素, 从大到小排序
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		return opsForZSet.reverseRangeByScore(key, min, max);
	}

	/**
	 * 根据Score值查询集合元素, 从大到小排序
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max) {
		return opsForZSet.reverseRangeByScoreWithScores(key, min, max);
	}

	/**
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zReverseRangeByScore(String key, double min, double max, long start, long end) {
		return opsForZSet.reverseRangeByScore(key, min, max, start, end);
	}

	/**
	 * 根据score值获取集合元素数量
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long zCount(String key, double min, double max) {
		return opsForZSet.count(key, min, max);
	}

	/**
	 * 获取集合大小
	 * 
	 * @param key
	 * @return
	 */
	public Long zSize(String key) {
		return opsForZSet.size(key);
	}

	/**
	 * 获取集合大小
	 * 
	 * @param key
	 * @return
	 */
	public Long zZCard(String key) {
		return opsForZSet.zCard(key);
	}

	/**
	 * 获取集合中value元素的score值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Double zScore(String key, String value) {
		return opsForZSet.score(key, value);
	}

	/**
	 * 移除指定索引位置的成员
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Long zRemoveRange(String key, long start, long end) {
		return opsForZSet.removeRange(key, start, end);
	}

	/**
	 * 根据指定的score值的范围来移除成员
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long zRemoveRangeByScore(String key, double min, double max) {
		return opsForZSet.removeRangeByScore(key, min, max);
	}

	/**
	 * 获取key和otherKey的并集并存储在destKey中
	 * 
	 * @param key
	 * @param otherKey
	 * @param destKey
	 * @return
	 */
	public Long zUnionAndStore(String key, String otherKey, String destKey) {
		return opsForZSet.unionAndStore(key, otherKey, destKey);
	}

	/**
	 * 
	 * @param key
	 * @param otherKeys
	 * @param destKey
	 * @return
	 */
	public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
		return redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
	}

	/**
	 * 交集
	 * 
	 * @param key
	 * @param otherKey
	 * @param destKey
	 * @return
	 */
	public Long zIntersectAndStore(String key, String otherKey, String destKey) {
		return opsForZSet.intersectAndStore(key, otherKey, destKey);
	}

	/**
	 * 交集
	 * 
	 * @param key
	 * @param otherKeys
	 * @param destKey
	 * @return
	 */
	public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		return opsForZSet.intersectAndStore(key, otherKeys, destKey);
	}

	/**
	 * 
	 * @param key
	 * @param options
	 * @return
	 */
	public Cursor<TypedTuple<String>> zScan(String key, ScanOptions options) {
		return opsForZSet.scan(key, options);
	}

	public Integer getInt(String key) {
		String result = get(key);
		if (result == null) {
			return null;
		}
		return Integer.valueOf(result);
	}
}
