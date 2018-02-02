package com.waston.controller.manage;

import com.waston.common.Consts;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        ServerResponse<User> serverResponse = userService.login(username, password);
        if(serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if(user.getRole() == Consts.ADMIN_ROLE) {
                String sessionId = request.getSession().getId();
                //回写cookie
                CookieUtil.addCookie(sessionId, response);
                //序列化
                ShardedRedisUtil.setEx(sessionId, JsonUtil.objectToJson(user), Consts.SESSION_EXPIRE_TIME);
            } else {
                return ServerResponse.createByError("普通用户, 权限不够");
            }
        }
        return serverResponse;
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
                                    @RequestParam(value = "pageNum", defaultValue = "10")int pageSize) {
        return userService.listUsers(pageNum, pageSize);
    }
}
