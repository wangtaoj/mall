package com.waston.utils;

import org.junit.Test;

import java.util.Date;

/**
 * @Author wangtao
 * @Date 2018/1/23
 **/
public class DateUtilTest {

    @Test
    public void testDateUtil() {
        String str = "2018-01-23 17:04:33";
        Date date = DateUtil.parseToDate(str);
        System.out.println(DateUtil.toString(date));
    }

}