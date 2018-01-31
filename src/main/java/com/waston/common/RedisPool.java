package com.waston.common;

import com.waston.utils.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 连接池
 * @Author wangtao
 * @Date 2018/1/29
 **/
public class RedisPool {

    //jedis连接池
    private static JedisPool pool;

    //最大连接数
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getProperty("redis.max.total", "20"));

    //在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle", "20"));

    //在jedispool中最小的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle", "20"));

    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow","true"));

    //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return","true"));

    //redis ip
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    //redis port
    private static Integer redisPort = Integer.valueOf(PropertiesUtil.getProperty("redis.port", "6379"));

    //redis连接超时时间, 单位为毫秒
    private static final int TIME_OUT = 1000;


    /**
     * 初始化连接池
     */
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        pool = new JedisPool(config,redisIp,redisPort,TIME_OUT * 10);
    }

    static{
        initPool();
    }

    /**
     * 返回Redis连接
     * @return
     */
    public static Jedis getJedis(){
        return pool.getResource();
    }


    /**
     * 将出现故障的redis连接返回连接池中
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    /**
     * 正常返回
     * @param jedis
     */
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

}

