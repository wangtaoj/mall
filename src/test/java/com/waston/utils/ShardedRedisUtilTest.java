package com.waston.utils;

import org.junit.Test;

/**
 * @Author wangtao
 * @Date 2018/2/1
 **/
public class ShardedRedisUtilTest {
    @Test
    public void set() throws Exception {
        for (int i = 1; i <= 10; i++) {
            ShardedRedisUtil.set("key-" + i, String.valueOf(i));
        }
    }

    @Test
    public void get() {
        System.out.println(ShardedRedisUtil.get("key-1"));
        System.out.println(ShardedRedisUtil.get("key-2"));
    }

    @Test
    public void testSet() {
        String value = ShardedRedisUtil.set("aa", "bb", 30);
        System.out.println(value);
    }

}