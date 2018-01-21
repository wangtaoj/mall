package com.waston.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
public class DateUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String toString(Date date) {
        if(date == null)
            return null;
        DateFormat dateFormat = new SimpleDateFormat(STANDARD_FORMAT);
        return dateFormat.format(date);
    }

}
