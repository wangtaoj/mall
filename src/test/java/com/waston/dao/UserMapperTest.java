package com.waston.dao;

import com.waston.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author wangtao
 * @date 2018-2018/1/12-20:45
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void checkUsername() throws Exception {
        System.out.println("result = " + userMapper.checkUsername("admin"));
    }

    @Test
    public void selectByUsernameAndPassword() {
        String username = "admin";
        String password = "427338237BD929443EC5D48E24FD2B1";
        User user = userMapper.selectByUsernameAndPassword(username, password);
        System.out.println("result = " + user);
    }



}