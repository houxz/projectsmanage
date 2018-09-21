package com.emg.projectsmanage.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.SerializationUtil;
import com.emg.projectsmanage.ctrl.ProcessesManageCtrl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisCache implements Cache {
	
	private static final Logger log = LoggerFactory.getLogger(ProcessesManageCtrl.class);

	/**
	 * 缓存是否启用
	 */
	private Boolean enabled = false;

	/**
	 * 缓存key值前缀，一般为项目名称
	 */
	private String proName = new String();

	/**
	 * 虚拟缓存槽 用于负载均衡计算缓存槽使用
	 */
	private TreeMap<Long, RedisShard> virtualShards = new TreeMap<Long, RedisShard>();

	/**
	 * 实际缓存槽
	 */
	private HashSet<RedisShard> practicalShards = new HashSet<RedisShard>();

	/**
	 * 缓存槽列表 Map格式，用于从配置文件中读取配置
	 */
	private List<Map<String, String>> shards = new ArrayList<Map<String, String>>();

	/**
	 * 虚拟化之后所有缓存服务器数量
	 */
	private Integer virtualNodeNum = 100;

	/**
	 * redis连接超时时间
	 */
	private Integer timeout = 20000;

	@PostConstruct
	public void init() {
		if (!this.enabled)
			return;
		try {
			this.virtualShards = new TreeMap<Long, RedisShard>();
			for (Map<String, String> shard : this.shards) {

				if (!shard.get("enabled").equalsIgnoreCase("true"))
					continue;

				final RedisShard shardInfo = new RedisShard(shard.get("ip"), Integer.valueOf(shard.get("port")),
						shard.get("password"));

				practicalShards.add(shardInfo);

				for (int n = 0; n < virtualNodeNum; n++)
					virtualShards.put(MurMurHash.Hash(shard.toString() + "-" + n), shardInfo);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 一致性Hash算法 基于key值，获取相应的缓存服务器
	 * 
	 * @param key
	 *            缓存的key值
	 * @return
	 */
	private Jedis getRedisShard(String key) {
		Jedis ret = null;
		try {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxTotal(10);
			RedisShard shard = null;
			SortedMap<Long, RedisShard> tail = virtualShards.tailMap(MurMurHash.Hash(key));
			if (tail == null || tail.size() == 0) {
				shard = virtualShards.get(virtualShards.firstKey());
			} else {
				shard = tail.get(tail.firstKey());
			}
			if (shard.getPassword() != null && !shard.getPassword().isEmpty()) {
				JedisPool pool = new JedisPool(jedisPoolConfig, shard.getHost().toString(),
						Integer.valueOf(shard.getPort()), timeout, shard.getPassword());
				ret = pool.getResource();
				pool.close();
			} else {
				JedisPool pool = new JedisPool(jedisPoolConfig, shard.getHost().toString(),
						Integer.valueOf(shard.getPort()), timeout);
				ret = pool.getResource();
				pool.close();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 判断缓存是否已经存在
	 * 
	 * @param key
	 * @return true：存在；false：不存在
	 */
	public Boolean exist(String key) {
		if (!this.enabled)
			return false;

		Boolean ret = false;
		try {
			key = String.format("%s-%s", proName, key);
			Jedis jedis = getRedisShard(key);
			if(jedis.exists(key)) {
				ret = true;
			} else {
				ret = false;
			}
			jedis.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 缓存数据，如果已经存在则覆盖原值
	 * 
	 * @param key
	 * @return true：缓存成功 ；false：缓存失败
	 */
	public Boolean cache(String key, Object value) {
		if (!this.enabled)
			return false;

		Boolean ret = false;
		try {
			key = String.format("%s-%s", proName, key);
			Jedis jedis = getRedisShard(key);
			if(jedis.set(key.getBytes(), SerializationUtil.serialize(value)).equals("OK")) {
				log.debug("Create Redis Cache: " + key);
				ret = true;
			} else {
				log.debug("Fail to Create Redis Cache: " + key);
				ret = false;
			}
			jedis.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 缓存数据，如果已经存在则覆盖原值
	 * 
	 * @param key
	 * @return true：缓存成功 ；false：缓存失败
	 */
	public Boolean cacheList(String key, List<?> value) {
		if (!this.enabled)
			return false;

		Boolean ret = false;
		try {
			key = String.format("%s-%s", proName, key);
			Jedis jedis = getRedisShard(key);
			if(jedis.set(key.getBytes(), SerializationUtil.serialize(value)).equals("OK")) {
				log.debug("Create Redis Cache: " + key);
				ret = true;
			} else {
				log.debug("Fail to Create Redis Cache: " + key);
				ret = false;
			}
			jedis.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return 缓存数据
	 */
	public Object get(String key) {
		if (!this.enabled)
			return null;

		Object ret = new Object();
		try {
			key = String.format("%s-%s", proName, key);
			Jedis jedis = getRedisShard(key);
			ret = SerializationUtil.deserialize(jedis.get(key.getBytes()));
			jedis.close();
			log.debug("Get Redis Cache: " + key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 * @return true：删除成功 ；false：删除失败
	 */
	public Boolean delete(String key) {
		if (!this.enabled)
			return false;

		Boolean ret = false;
		try {
			Jedis jedis = getRedisShard(key);
			ret = jedis.del(key).compareTo(0L) > 0;
			jedis.close();
			log.debug("Remove Redis Cache: " + key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 删除多个缓存
	 * 
	 * @param keys
	 * @return true：删除成功 ；false：删除失败
	 */
	public Boolean deleteList(Set<String> keys) {
		if (!this.enabled)
			return false;

		Boolean ret = false;
		try {
			for (String key : keys) {
				Jedis jedis = getRedisShard(key);
				ret = ret && (jedis.del(key).compareTo(0L) > 0);
				jedis.close();
				log.debug("Remove Redis Cache: " + key);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return ret;
	}

	/**
	 * 基于bound删除所有缓存服务器上的相关缓存
	 * 
	 * @param pattern
	 * @return
	 */
	public Boolean deleteKeysByBounds(Set<String> bounds) {
		if (!this.enabled)
			return false;

		Boolean ret = true;
		try {
			for (RedisShard shard : this.practicalShards) {
				JedisPool pool = shard.getPassword() != null && !shard.getPassword().isEmpty()
						? new JedisPool(new JedisPoolConfig(), shard.getHost(), shard.getPort(), this.timeout,
								shard.getPassword())
						: new JedisPool(new JedisPoolConfig(), shard.getHost().toString(),
								Integer.valueOf(shard.getPort()), timeout);
				Jedis jedis = pool.getResource();
				for (String bound : bounds) {
					Set<String> keys = jedis.keys(proName + "*" + bound + "*");
					for (String key : keys) {
						jedis = getRedisShard(key);
						if (jedis.del(key).compareTo(0L) > 0) {
							ret = ret && true;
							log.debug("Remove Redis Cache: " + key);
						} else {
							ret = false;
							log.error("Fail to remove Redis Cache: " + key);
						}

					}
				}
				pool.close();
				jedis.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	public List<Map<String, String>> getShards() {
		return shards;
	}

	public void setShards(List<Map<String, String>> shards) {
		this.shards = shards;
	}

	public Integer getVirtualNodeNum() {
		return virtualNodeNum;
	}

	public void setVirtualNodeNum(Integer virtualNodeNum) {
		this.virtualNodeNum = virtualNodeNum;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public ValueWrapper get(Object key) {
		return null;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public void put(Object key, Object value) {
		
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}

	@Override
	public void evict(Object key) {
		
	}

	@Override
	public void clear() {
		
	}
}
