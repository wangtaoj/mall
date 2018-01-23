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

    //购物车记录选中标志
    public static final Integer CART_CHECK = 1;
    public static final Integer CART_UNCHECK = 0;

    //购物车添加或者更新记录时是否超过商品库存的一个字段
    public static final String CART_SUCCESS = "LIMIT_NUM_SUCCESS";
    public static final String CART_FAIL = "LIMIT_NUM_FAIL";

    /**
     * 交易状态, 支付宝官方提供, 回调返回结果会包含该字段, 可用来判断订单是否成功付款
     */
    public interface  TRADE_STATUS{
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY"; //交易创建，等待买家付款
        String TRADE_CLOSED = "TRADE_CLOSED"; //未付款交易超时关闭，或支付完成后全额退款
        String TRADE_SUCCESS = "TRADE_SUCCESS"; //交易支付成功
        String TRADE_FINISHED = "TRADE_FINISHED"; //交易结束，不可退款
    }

    /**
     * 订单状态
     */
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;
        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }
    }

    /**
     * 支付平台类型枚举类
     */
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }



}
