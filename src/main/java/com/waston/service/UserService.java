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

}
