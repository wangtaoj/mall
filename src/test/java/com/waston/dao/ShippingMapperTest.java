package com.waston.dao;

import com.waston.pojo.Shipping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class ShippingMapperTest {

    @Autowired
    private ShippingMapper shippingMapper;

    @Test
    public void insert() throws Exception {
        Shipping shipping = new Shipping();
        shipping.setUserId(22);
        shipping.setReceiverName("汪涛");
        shipping.setReceiverMobile("15700759346");
        shipping.setReceiverPhone("15700759346");
        shipping.setReceiverProvince("湖南省");
        shipping.setReceiverCity("长沙市");
        shipping.setReceiverDistrict("天心区");
        shipping.setReceiverAddress("中南林业科技大学");
        shipping.setReceiverZip("410000");
        Date date = new Date();
        shipping.setCreateTime(date);
        shipping.setUpdateTime(date);
        shippingMapper.insert(shipping);
        System.out.println(shipping.getId());
    }

}