package com.waston.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 封装返回的json数据
 * @author wangtao
 * @date 2018-2018/1/10-21:55
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)  //属性为null的字段不序列化
public class ServerResponse<T> {

    //状态 0:成功  1: 失败  -----ResponseCode枚举类
    private int status;

    //提示消息
    private String msg;

    //数据
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus(),msg, data);
    }

    public static <T> ServerResponse<T> createBySuccessMsg(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus(), msg);
    }

    public static <T> ServerResponse<T> createByError(String msg) {
        return new ServerResponse<>(ResponseCode.ERROR.getStatus(), msg);
    }

    public static <T> ServerResponse<T> createByError(int status, String msg) {
        return new ServerResponse<>(status, msg);
    }

    @JsonIgnore //忽略该字段
    public boolean isSuccess() {
        return status == 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
