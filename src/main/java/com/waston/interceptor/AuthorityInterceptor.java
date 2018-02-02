package com.waston.interceptor;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.utils.CookieUtil;
import com.waston.utils.JsonUtil;
import com.waston.utils.ShardedRedisUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * 后台权限拦截器
 * @Author wangtao
 * @Date 2018/2/2
 **/
public class AuthorityInterceptor implements HandlerInterceptor{

    /**
     * 拦截的请求之前执行, 如果返回true, 则继续执行controller, 否则终止
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginToken = CookieUtil.getSessionKey(request);
        User user = null;
        if(loginToken != null) {
            user = JsonUtil.jsonToObject(ShardedRedisUtil.get(loginToken), User.class);
        }
        //权限通过
        if(user != null && user.getRole() == Consts.ADMIN_ROLE) {
            return true;
        }
        response.reset(); //清空头, 以及缓冲
        response.setContentType("application/json;charset=UTF-8");
        Writer out = response.getWriter();
        ServerResponse serverResponse;
        if(user == null) {
            serverResponse = ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(),"还未登录");
        } else {
            serverResponse = ServerResponse.createByError("普通用户, 无权限操作!");
        }
        String json = JsonUtil.objectToJson(serverResponse);
        out.write(json != null ? json : "\"msg\":\"server error\"");
        out.flush();
        out.close();
        return false;
    }

    /**
     * 拦截器放行后, 执行完controller之后调用此方法
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 所有的工作完成后, 比如controller返回model and view全部渲染完毕之后再执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
