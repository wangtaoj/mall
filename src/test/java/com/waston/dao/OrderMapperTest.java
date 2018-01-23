package com.waston.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author wangtao
 * @Date 2018/1/23
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void selectByOrderNo() throws Exception {
        System.out.println(orderMapper.selectByOrderNo(1491830695216L));
    }

    @Test
    public void selectByOrderNoAndUserId() throws Exception {
    }

}