package com.waston.filter;

import com.waston.common.Consts;
import com.waston.utils.CookieUtil;
import com.waston.utils.RedisUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用来更新redis中用户信息的有效期
 * @Author wangtao
 * @Date 2018/1/31
 **/
@WebFilter(filterName = "sessionExpire", urlPatterns = "*.do")
public class SessionExpireFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();
        if(!path.contains("logout.do")) {
            //重置用户信息的有效期
            String loginToken = CookieUtil.getSessionKey(req);
            if(loginToken != null) {
                RedisUtil.expire(loginToken, Consts.SESSION_EXPIRE_TIME);
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
