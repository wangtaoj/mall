package com.waston.utils;

import org.junit.Test;

import java.util.Random;

/**
 * @author wangtao
 * @date 2018-2018/1/18-23:10
 **/
public class MD5UtilTest {
    @Test
    public void md5() throws Exception {

        System.out.println(MD5Util.md5("111111"));
        System.out.println(MD5Util.md5("123456"));
    }

    @Test
    public void testt() {
        Long orderNo = Long.valueOf(System.currentTimeMillis() + "" + new Random().nextInt(100));
        System.out.println(orderNo);
    }

}