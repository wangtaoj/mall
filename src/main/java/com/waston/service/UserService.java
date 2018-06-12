package com.waston.service;

import com.waston.common.ServerResponse;
import com.waston.pojo.User;

/**
 * 业务层用户接口
 * @author wangtao
 * @date 2018-2018/1/12-20:10
 **/

public interface UserService {

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 用户注册
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 检查用户
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);

    /**
     * 获取用户的问题，重置密码所用
     * @param username
     * @return
     */
    ServerResponse<String> getQuestion(String username);

    /**
     * 检查问题和答案是否匹配
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    /**
     * 忘记密码时重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 登录状态下重置密码
     * @param passwordOld
     * @param passwordNew
     * @param username
     * @return
     */
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, String username);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    ServerResponse<User> updateUserInfo(User user);

    /**
     * 获取用户详细信息
     * @param id
     * @return
     */
    ServerResponse<User> getUserInfo(Integer id);

    /**
     * 分页获取用户列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse listUsers(int pageNum, int pageSize);

    /**
     * 删除用户
     * @param id
     * @return
     */
    ServerResponse removeUser(Integer id);
}
