package com.waston.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void isExist() throws Exception {
        System.out.println(productMapper.isExist(26));
    }

    @Test
    public void selectByNameAndId() {
        System.out.println(productMapper.selectByNameAndId(null, null));
    }

}