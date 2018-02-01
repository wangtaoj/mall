package com.waston.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.waston.common.Consts;
import com.waston.common.ServerResponse;
import com.waston.dao.UserMapper;
import com.waston.pojo.User;
import com.waston.service.UserService;
import com.waston.utils.MD5Util;
import com.waston.utils.ShardedRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        //检查用户名
        ServerResponse<String> validRes = checkValid(user.getUsername(), Consts.TYPE_USERNAME);
        if(!validRes.isSuccess())
            return validRes;

        //检查用户邮箱
        validRes = checkValid(user.getEmail(), Consts.TYPE_EMAIL);
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

    public ServerResponse<String> checkValid(String str, String type) {
        if(Objects.equals(type, Consts.TYPE_USERNAME)) {
            if(userMapper.checkUsername(str) > 0)
                return ServerResponse.createByError("用户已存在!");
            return ServerResponse.createBySuccessMsg("校验成功!");
        } else if (Objects.equals(type, Consts.TYPE_EMAIL)) {
            if(userMapper.checkEmail(str) > 0)
                return ServerResponse.createByError("邮箱已存在!");
            return ServerResponse.createBySuccessMsg("校验成功!");
        } else {
            return ServerResponse.createByError("type参数错误!");
        }
    }

    public ServerResponse<String> getQuestion(String username) {
        ServerResponse<String> validRes = checkValid(username, Consts.TYPE_USERNAME);
        if(validRes.isSuccess()) {
            return ServerResponse.createByError("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isEmpty(question)) {
            return ServerResponse.createByError("该用户未设置找回密码问题");
        }
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        ServerResponse<String> response = checkValid(username, Consts.TYPE_USERNAME);
        if(response.isSuccess())
            return ServerResponse.createByError("用户不存在");
        if(userMapper.checkAnswer(username, question, answer) > 0) {
            //使用UUID生成一个用户token返回, 并且缓存下来
            String token = UUID.randomUUID().toString();
            //十分钟有效
            ShardedRedisUtil.setEx(Consts.TOKEN_PREFIX, token, Consts.TOKEN_EXPIRE_TIME);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByError("问题答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isEmpty(forgetToken))
            return ServerResponse.createByError("请提交token参数");
        ServerResponse validRes = checkValid(username, Consts.TYPE_USERNAME);
        if(validRes.isSuccess()) {
            return ServerResponse.createByError("用户不存在");
        }
        String cacheToken = ShardedRedisUtil.get(Consts.TOKEN_PREFIX + username);
        if(cacheToken == null)
            return ServerResponse.createByError("token已经失效");
        if(!Objects.equals(cacheToken, forgetToken))
            return ServerResponse.createByError("token不正确, 请重新获取");
        if(userMapper.updatePassword(username, MD5Util.md5(passwordNew)) > 0) {
            return ServerResponse.createBySuccessMsg("修改密码成功");
        }
        return ServerResponse.createByError("修改密码操作失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, String username) {
        //先校验旧密码
        if(userMapper.checkPassword(username, MD5Util.md5(passwordOld)) <= 0)
            return ServerResponse.createByError("原密码输入错误");

        if(StringUtils.isEmpty(passwordNew))
            return ServerResponse.createByError("请输入新密码");
        //修改密码
        if(userMapper.updatePassword(username, MD5Util.md5(passwordNew)) > 0)
            return ServerResponse.createBySuccessMsg("密码修改成功");
        return ServerResponse.createByError("密码修改失败");
    }

    public ServerResponse<User> updateUserInfo(User user) {
        //检查邮箱是否有效, 可能未修改还是原来的邮箱, 所以不能重用checkEmail方法, 需要再添加id != #{id}
        if(userMapper.checkEmailById(user.getId(), user.getEmail()) > 0) {
            return ServerResponse.createByError("邮箱已被使用, 请更换");
        }
        //更新
        if(userMapper.updateByPrimaryKeySelective(user) > 0)
            return ServerResponse.createBySuccess("更新信息成功", user);
        return ServerResponse.createByError("更新信息失败");
    }

    public ServerResponse<User> getUserInfo(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null) {
            return ServerResponse.createByError("此用户不存在");
        }
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse listUsers(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectByPage();
        PageInfo pageResult = new PageInfo<>(users);
        return ServerResponse.createBySuccess(pageResult);
    }

}
