package com.waston.common;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @Author wangtao
 * @Date 2018/1/29
 **/
public class RedisPoolTest {

    @Test
    public void getJedis() throws Exception {
        Jedis jedis = RedisPool.getJedis();
        jedis.set("test", "value");
        System.out.println(jedis.get("test"));
        RedisPool.returnResource(jedis);
    }

}