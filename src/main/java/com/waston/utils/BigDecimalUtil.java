package com.waston.utils;

import java.math.BigDecimal;

/**
 * @Author wangtao
 * @Date 2018/1/21
 **/
public class BigDecimalUtil {

    /**
     * 两个浮点数相加
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal add(double num1, double num2) {
        return new BigDecimal(Double.toString(num1)).add(new BigDecimal(Double.toString(num2)));
    }

    /**
     * 两个浮点相减
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal subtract(double num1, double num2) {
        return new BigDecimal(Double.toString(num1)).subtract(new BigDecimal(Double.toString(num2)));
    }

    /**
     * 两个浮点数相乘
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal multiply(double num1, double num2) {
        return new BigDecimal(Double.toString(num1)).multiply(new BigDecimal(Double.toString(num2)));
    }

    /**
     * 两个浮点数相除, 保留两位小数, 四舍五入
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal divide(double num1, double num2) {
        return new BigDecimal(Double.toString(num1)).divide(new BigDecimal(Double.toString(num2)), 2, BigDecimal.ROUND_HALF_UP);
    }

}
