package com.waston.common;

/**
 * 状态枚举类
 * @author wangtao
 * @date 2018-2018/1/12-20:22
 **/
public enum ResponseCode {
    SUCCESS(0, "success"),
    ERROR(1, "error");

    private final int status;

    private final String desc;

    ResponseCode(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
