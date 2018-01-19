package com.waston.common;

import org.junit.Test;

import java.util.concurrent.TimeUnit;


/**
 * @author wangtao
 * @date 2018-2018/1/18-16:03
 **/
public class TokenCacheTest {

    @Test
    public void testCache() {
        TokenCache.put("aaa", "aaaaa");
        System.out.println(TokenCache.get("aaa"));
        System.out.println(TokenCache.get("a"));
    }

    /**
     * 测试缓存过期, 为了测试把过期时间换成了5秒
     */
    @Test
    public void testCacheValid() {
        TokenCache.put("aaa", "aaaaa");
        System.out.println(TokenCache.get("aaa"));
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(TokenCache.get("aaa"));
    }

}