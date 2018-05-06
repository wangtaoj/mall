package com.waston.controller.manage;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 管理员用户接口
 * @author wangtao
 * @date 2018-2018/1/18-22:19
 **/
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse login(String username, String password, HttpSession session) {
        ServerResponse<User> response = userService.login(username, password);
        if(response.isSuccess()) {
            User user = response.getData();
            if(user.getRole() == Consts.ADMIN_ROLE) {
                session.setAttribute(Consts.CURRENT_USER, user);
                return response;
            }
            return ServerResponse.createByError("普通用户, 权限不够");
        }
        return response;
    }

    @RequestMapping(value = "/get_username.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse login(HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null)
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        if(currentUser.getRole() != Consts.ADMIN_ROLE)
            return ServerResponse.createByError("普通用户, 权限不够");
        return ServerResponse.createBySuccess(currentUser);
    }

    /**
     * 注销
     * @param session
     * @return
     */
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse logout(HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null)
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        if(currentUser.getRole() != Consts.ADMIN_ROLE)
            return ServerResponse.createByError("普通用户, 权限不够");
        session.removeAttribute(Consts.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 分页获取用户接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse listUsers(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "8")int pageSize,
                                    HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null)
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        if(currentUser.getRole() != Consts.ADMIN_ROLE)
            return ServerResponse.createByError("普通用户, 权限不够");
        return userService.listUsers(pageNum, pageSize);
    }

}
