
package com.waston.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Random;

/**
 * 
 * @Author wangtao
 * @Date   2018/1/20
 */
public class NameUtil {

    public static final String DTLONG = "yyyyMMddHHmmss";

    public static final String DTSHORT = "yyyy/MM/dd";
	
    /**
     * 用日期作为目录
     * @return
     */
    public static String getDateDir() {
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(DTSHORT);
        return df.format(date);
    }
    
    /**
     * 用日期作为名字, 精确到秒, 并且随机生成三位数字作为补充
     * @return
     */
    public static String getName() {
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(DTLONG);
        String res = df.format(date) + randomNumber(3);
        return res;
    }

    /**
     * 生成随机数字
     * @param len
     * @return
     */
    private static String randomNumber(int len) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 获取文件后缀名
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if(index != -1)
            return fileName.substring(index);
        return fileName;
    }
}
