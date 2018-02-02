package com.waston.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author wangtao
 * @Date 2018/1/30
 **/
public class JsonUtilTest {

    @Test
    public void objectToJson() {
        JsonBean jsonBean = new JsonBean();
        jsonBean.setName("waston");
        jsonBean.setAge(20);
        jsonBean.setBirthday(new Date());
        System.out.println(JsonUtil.objectToJson(jsonBean));
    }

    @Test
    public void jsonToObject() {
        String json = "{\"name\":\"waston\",\"age\":20,\"birthday\":\"2018-01-30 11:30:54\"}";
        JsonBean jsonBean = JsonUtil.jsonToObject(json, JsonBean.class);
        System.out.println(jsonBean);
    }

    @Test
    public void jsonToList() {
        JsonBean jsonBean = new JsonBean();
        jsonBean.setName("waston");
        jsonBean.setAge(20);
        jsonBean.setBirthday(new Date());

        JsonBean jsonBean1 = new JsonBean();
        jsonBean1.setName("jane");
        jsonBean1.setAge(30);
        jsonBean1.setBirthday(new Date());

        List<JsonBean> list = new ArrayList<>();
        list.add(jsonBean);
        list.add(jsonBean1);
        String json = JsonUtil.objectToJson(list);
        System.out.println(json);

        List<JsonBean> list1 = JsonUtil.jsonToList(json, JsonBean.class);
        System.out.println(list1);

    }

}