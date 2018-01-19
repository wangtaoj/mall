package com.waston.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用于token的缓存
 * @author wangtao
 * @date 2018-2018/1/18-15:42
 **/
public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";

    /**
     * 建立一个缓存容器对象, 用来缓存内容
     * 初始化容量大小为1000条, 最大容量为10000条
     * 数据过期时间为5分钟
     * 当容量达到上限时默认使用LRU算法回收记录
     */
    private static LoadingCache<String, String> cache = CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000).expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                //当缓存中没有缓存<key, value>对时, 将调用此方法
                //这里简单实现返回"null"即可, 避免getUnchecked()方法返回null值抛异常
                @Override
                public String load(String s) {
                    return "null";
                }
            });

    /**
     * 放值
     * @param value
     */
    public static void put(String key, String value) {
        cache.put(key, value);
    }

    /**
     * 取值
     * @param key
     * @return
     */
    public static String get(String key) {
        //因为load方法未抛异常, 因此可以使用getUnchecked方法来获取值, 否则使用get获取并且捕获异常
        String value = cache.getUnchecked(key);
        if(Objects.equals("null", value))
            return null;
        return value;
    }
}
