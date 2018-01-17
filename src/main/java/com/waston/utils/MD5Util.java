package com.waston.utils;

import com.waston.common.Consts;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author wangtao
 * @date 2018-2018/1/12-22:20
 **/
public class MD5Util {

    public static String md5(String password) {
        try {
            String md5 = DigestUtils.md5DigestAsHex((password + Consts.SALT).getBytes("UTF-8"));
            return md5;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
