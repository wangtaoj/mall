package com.waston.controller.portal;

import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author wangtao
 * @date 2018-2018/1/10-21:49
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse response = userService.login(username, password);
        //登录成功, 将当前用户存入session中
        if(response.isSuccess()) {
            session.setAttribute("user", response.getData());
        }
        return response;
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "/register.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<String> register(User user) {
        System.out.println(user);
        return userService.register(user);
    }


}
