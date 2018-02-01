package com.waston.controller.portal;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.UserService;
import com.waston.utils.CookieUtil;
import com.waston.utils.JsonUtil;
import com.waston.utils.ShardedRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author wangtao
 * 2018-2018/1/10-21:49
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        ServerResponse serverResponse = userService.login(username, password);
        //登录成功, 将当前用户序列化到redis中
        if(serverResponse.isSuccess()) {
            String sessionId = request.getSession().getId();
            //回写cookie
            CookieUtil.addCookie(sessionId, response);
            //序列化
            ShardedRedisUtil.setEx(sessionId, JsonUtil.objectToJson(serverResponse.getData()), Consts.SESSION_EXPIRE_TIME);
        }
        return serverResponse;
    }

    /**
     * 注销接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        //删除登录cookie && 移除redis用户信息
        String loginToken = CookieUtil.getSessionKey(request);
        if (loginToken != null) {
            ShardedRedisUtil.del(loginToken);
            CookieUtil.removeCookie(request, response);
            return ServerResponse.createBySuccess("退出成功");
        }
        return ServerResponse.createByError("还未登录");

    }

    /**
     * 注册接口
     * @param user
     * @return
     */
    @RequestMapping(value = "/register.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return userService.register(user);
    }

    /**
     * 检查用户是否有效, 如/check_valid.do?str=admin&type=username
     * @param str   值为username or email, 由type决定
     * @param type  type: username or email
     * @return
     */
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }

    /**
     * 获取登录用户信息接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String loginToken = CookieUtil.getSessionKey(request);
        User user = null;
        if(loginToken != null) {
            user = JsonUtil.jsonToObject(ShardedRedisUtil.get(loginToken), User.class);
        }
        if(user == null) {
            return ServerResponse.createByError("用户未登录,无法获取当前用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 忘记密码时, 获取问题用来重置密码使用的接口
     * @param username
     * @return
     */
    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> getQuestion(String username) {
        return userService.getQuestion(username);
    }

    /**
     * 检查问题答案是否匹配的接口
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码时重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态下修改密码
     * @param passwordOld
     * @param passwordNew
     * @param request
     * @return
     */
    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, HttpServletRequest request) {
        String loginToken = CookieUtil.getSessionKey(request);
        User user = null;
        if(loginToken != null) {
            user = JsonUtil.jsonToObject(ShardedRedisUtil.get(loginToken), User.class);
        }
        if(user == null)
            return ServerResponse.createByError("还未登录");
        return userService.resetPassword(passwordOld, passwordNew, user.getUsername());
    }

    /**
     * 更新用户信息
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse updateUserInfo(User user, HttpServletRequest request) {
        String loginToken = CookieUtil.getSessionKey(request);
        User currentUser = null;
        if(loginToken != null) {
            currentUser = JsonUtil.jsonToObject(ShardedRedisUtil.get(loginToken), User.class);
        }
        if(currentUser ==  null) {
            return ServerResponse.createByError("还未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        user.setCreateTime(currentUser.getCreateTime());
        user.setUpdateTime(new Date());
        user.setPassword(null); //设置为null, 不修改。
        ServerResponse response = userService.updateUserInfo(user);
        //更新成功, 更新redis的用户信息
        if(response.isSuccess()) {
            user.setPassword(currentUser.getPassword());
            ShardedRedisUtil.setEx(loginToken, JsonUtil.objectToJson(user), Consts.SESSION_EXPIRE_TIME);
        }
        return response;
    }


    /**
     * 获取用户详细信息的接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<User> getUserDetail(HttpServletRequest request) {
        String loginToken = CookieUtil.getSessionKey(request);
        User user = null;
        if(loginToken != null) {
            user = JsonUtil.jsonToObject(ShardedRedisUtil.get(loginToken), User.class);
        }
        if(user == null)
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(),
                    "用户未登录,无法获取当前用户信息,status=10,强制登录");
        return userService.getUserInfo(user.getId());
    }

}
