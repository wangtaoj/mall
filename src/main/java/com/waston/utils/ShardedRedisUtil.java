package com.waston.utils;

import com.waston.common.ShardedRedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

/**
 * Redis工具类
 * @Author wangtao
 * @Date 2018/1/29
 **/
public class ShardedRedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardedRedisUtil.class);

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param expireTime
     * @return
     */
    public static boolean expire(String key,int expireTime){
        ShardedJedis jedis = null;
        boolean result = false;
        try {
            jedis = ShardedRedisPool.getJedis();
            if(jedis.expire(key,expireTime) > 0)
                result = true;
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            logger.error("expire key:{} error",key,e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    /**
     * 添加key的同时设置过期时间
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public static String setEx(String key,String value,int expireTime){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = ShardedRedisPool.getJedis();
            result = jedis.setex(key,expireTime,value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            logger.error("setex key:{} value:{} error",key,value,e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    /**
     * 添加key, value
     * @param key
     * @param value
     * @return
     */
    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = ShardedRedisPool.getJedis();
            result = jedis.set(key,value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            logger.error("set key:{} value:{} error",key,value,e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    /**
     * 获取key所对应的value
     * @param key
     * @return
     */
    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = ShardedRedisPool.getJedis();
            result = jedis.get(key);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            logger.error("get key:{} error",key,e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = ShardedRedisPool.getJedis();
            result = jedis.del(key);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            logger.error("del key:{} error",key,e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }
}
