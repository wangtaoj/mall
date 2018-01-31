package com.waston.utils;

import java.util.Date;

/**
 * @Author wangtao
 * @Date 2018/1/30
 **/
public class JsonBean {

    private String name;

    private Integer age;

    private Date birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
