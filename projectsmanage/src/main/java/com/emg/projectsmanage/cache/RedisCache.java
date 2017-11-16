package com.emg.projectsmanage.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.emg.projectsmanage.common.SerializationUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCache {
	private TreeMap<Long, HashMap<String, String>> nodes;
	private List<HashMap<String, String>> shards;

	/**
	 * 虚拟化之后所有缓存服务器数量
	 */
	private final int NODE_NUM = 100;

	/**
	 * 引入实际缓存服务器配置 初始化虚拟缓存服务器组 nodes
	 * 
	 * @param jedisPoolHostList
	 *            实际缓存服务器配置
	 */
	public RedisCache(ArrayList<HashMap<String, String>> jedisPoolHostList) {
		this.shards = jedisPoolHostList;
		this.nodes = new TreeMap<Long, HashMap<String, String>>();
		for (HashMap<String, String> shard : shards) {
			final HashMap<String, String> shardInfo = shard;

			for (int n = 0; n < NODE_NUM; n++)
				nodes.put(MurMurHash.Hash("SHARD-" + shard.get("host") + ":" + shard.get("port") + "-NODE-" + n), shardInfo);
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
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(10);
		HashMap<String, String> shard = new HashMap<String, String>();
		if (this.shards.size() == 1) {
			shard = this.shards.get(0);
		} else {
			SortedMap<Long, HashMap<String, String>> tail = nodes.tailMap(MurMurHash.Hash(key));
			if (tail.size() == 0) {
				shard = nodes.get(nodes.firstKey());
			}
			shard = tail.get(tail.firstKey());
		}
		JedisPool pool = new JedisPool(jedisPoolConfig, shard.get("host").toString(), Integer.valueOf(shard.get("port")));
		return pool.getResource();
	}

	/**
	 * 判断缓存是否已经存在
	 * 
	 * @param key
	 * @return true：存在；false：不存在
	 */
	public Boolean exist(String key) {
		Boolean ret = false;
		Jedis jedis = getRedisShard(key);
		ret = jedis.exists(key);
		jedis.close();
		return ret;
	}

	/**
	 * 缓存数据，如果已经存在则覆盖原值
	 * 
	 * @param key
	 * @return true：缓存成功 ；false：缓存失败
	 */
	public Boolean cache(String key, Object value) {
		Boolean ret = false;
		Jedis jedis = getRedisShard(key);
		ret = jedis.set(key.getBytes(), SerializationUtil.serialize(value)).equals("OK");
		jedis.close();
		return ret;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return 缓存数据
	 */
	public Object get(String key) {
		Object ret = new Object();
		Jedis jedis = getRedisShard(key);
		ret = SerializationUtil.deserialize(jedis.get(key.getBytes()));
		jedis.close();
		return ret;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 * @return true：删除成功 ；false：删除失败
	 */
	public Boolean delete(String key) {
		Boolean ret = false;
		Jedis jedis = getRedisShard(key);
		ret = jedis.del(key).compareTo(0L) > 0;
		jedis.close();
		return ret;
	}
}
