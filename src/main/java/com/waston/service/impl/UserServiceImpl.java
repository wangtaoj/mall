package com.waston.service.impl;

import com.waston.common.Consts;
import com.waston.common.ServerResponse;
import com.waston.dao.UserMapper;
import com.waston.pojo.User;
import com.waston.service.UserService;
import com.waston.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 业务层用户接口实现类
 * @author wangtao
 * @date 2018-2018/1/12-20:11
 **/
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        if(userMapper.checkUsername(username) <= 0) {
            return ServerResponse.createByError("用户名不存在!");
        }

        if(StringUtils.isEmpty(password))
            return ServerResponse.createByError("请输入密码!");

        User user = userMapper.selectByUsernameAndPassword(username, MD5Util.md5(password));
        if(user == null) {
            return ServerResponse.createByError("密码错误!");
        }
        //登录成功
        return ServerResponse.createBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(User user) {
        //校验用户填写的用户名和邮箱, 以及是否输入密码
        ServerResponse<String> validRes = checkValid(user);
        if(!validRes.isSuccess())
            return validRes;
        //插入到用户数据表中
        user.setRole(Consts.COMMON_ROLE);
        Date date = new Date();
        user.setPassword(MD5Util.md5(user.getPassword()));
        user.setCreateTime(date);
        user.setUpdateTime(date);
        if(userMapper.insertSelective(user) <= 0)
            return ServerResponse.createByError("注册失败!");
        return ServerResponse.createBySuccess("注册成功!");
    }

    private ServerResponse<String> checkValid(User user) {
        if(StringUtils.isEmpty(user.getPassword()))
            return ServerResponse.createByError("请输入密码!");

        if(userMapper.checkUsername(user.getUsername()) > 0)
            return ServerResponse.createByError("用户已存在!");

        if(userMapper.checkEmail(user.getEmail()) > 0)
            return ServerResponse.createByError("邮箱已存在!");

        return ServerResponse.createBySuccess("校验成功!");
    }



}
