package com.waston.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author wangtao
 * @Date 2018/1/21
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class CartMapperTest {

    @Autowired
    private CartMapper cartMapper;

    @Test
    public void selectByProductIdAndUserId() throws Exception {
        System.out.println(cartMapper.selectByProductIdAndUserId(26, 21));
    }

    @Test
    public void selectByUserId() throws Exception {
        System.out.println(cartMapper.selectByUserId(21));
    }

    @Test
    public void selectAllChecked() throws Exception {
        System.out.println(cartMapper.selectAllChecked(21));
    }

}