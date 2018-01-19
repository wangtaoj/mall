package com.waston.dao;

import com.waston.pojo.User;
import com.waston.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


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

    @Test
    public void checkEmail() {
        String email = "admin@happymmall.com";
        System.out.println(userMapper.checkEmail(email));
    }

    @Test
    public void selectQuestionByUsername() {
        String username = "admin";
        System.out.println(userMapper.selectQuestionByUsername(username));
    }

    @Test
    public void checkAnswer() {
        String username = "admin";
        String question = "问题";
        String answer = "答案";
        System.out.println(userMapper.checkAnswer(username,question, answer));
    }

    @Test
    public void updatePassword() {
        String username = "admin";
        String password = "111111";
        System.out.println(userMapper.updatePassword(username, MD5Util.md5(password)));
    }

    @Test
    public void checkPassword() {
        String username = "admin";
        String password = "111111";
        System.out.println(userMapper.checkPassword(username, MD5Util.md5(password)));
    }

    @Test
    public void checkEmailById() {
        System.out.println(userMapper.checkEmailById(13, "admin@happymmall.com"));
    }

}