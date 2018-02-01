package com.waston.common;

import com.waston.utils.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装ShardedJedisPool(分片连接池)
 * @Author wangtao
 * @Date 2018/2/1
 **/
public class ShardedRedisPool {

    //分片连接
    private static ShardedJedisPool pool;

    //最大连接数
    private static final int MAX_TOTAL = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));

    //在连接池中最大的idle状态(空闲的)的jedis实例的个数
    private static final int MAX_IDLE = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));

    //在连接池中最小的idle状态(空闲的)的jedis实例的个数
    private static final int MIN_IDLE = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));

    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static final boolean TEST_ON_BORROW = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow","true"));

    //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。
    private static boolean TEST_ON_RETURN = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));

    //redis连接超时时间, 单位为毫秒
    private static final int TIME_OUT = 1000;

    //redis host
    private static final String IP1 = PropertiesUtil.getProperty("redis.ip1");
    private static final String IP2 = PropertiesUtil.getProperty("redis.ip2");

    //redis port
    private static final int PORT1 = Integer.parseInt(PropertiesUtil.getProperty("redis.port1", "6379"));
    private static final int PORT2 = Integer.parseInt(PropertiesUtil.getProperty("redis.port2", "6380"));


    /**
     * 初始化连接池
     */
    private static void initPool() {
        //初始化连接池的配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMinIdle(MIN_IDLE);
        config.setTestOnBorrow(TEST_ON_BORROW);
        config.setTestOnReturn(TEST_ON_RETURN);
        //连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true
        config.setBlockWhenExhausted(true);

        List<JedisShardInfo> shards = new ArrayList<>();
        //超时时间5秒
        JedisShardInfo shardInfo1 = new JedisShardInfo(IP1, PORT1, TIME_OUT * 5);
        JedisShardInfo shardInfo2 = new JedisShardInfo(IP2, PORT2, TIME_OUT * 5);
        shards.add(shardInfo1);
        shards.add(shardInfo2);

        /*
         * Hashing.MURMUR_HASH: 哈希一致性策略(默认)
         * Sharded.DEFAULT_KEY_TAG_PATTERN(分片算法所依据key的形式, 具体作用还不清楚)
         */
        pool = new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    /**
     * 从池中获取一个连接
     * @return
     */
    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    /**
     * 归还redis连接资源(坏的)
     * @param jedis
     */
    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    /**
     * 归还redis连接资源(好的)
     * @param jedis
     */
    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

}
