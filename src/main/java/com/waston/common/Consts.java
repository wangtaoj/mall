package com.waston.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author wangtao
 * @date 2018-2018/1/12-22:05
 **/
public class Consts {

    //普通用户角色
    public static final int COMMON_ROLE = 1;

    //管理员角色
    public static final int ADMIN_ROLE = 0;

    //md5算法盐值
    public static final String SALT = "defgujih%$#^!^*$";

    // checkValid type=username
    public static final String TYPE_USERNAME = "username";

    // checkValid type=email
    public static final String TYPE_EMAIL = "email";

    //session存取当前用户的key值
    public static final String CURRENT_USER = "currentUser";

    //商品在售状态
    public static final int ON_SALE = 1;

    //商品排序规则
    public static final Set<String> ORDER_SET = Sets.newHashSet("price_asc", "price_desc");

}
