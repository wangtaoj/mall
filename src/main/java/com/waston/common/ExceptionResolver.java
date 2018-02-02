package com.waston.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 * @Author wangtao
 * @Date 2018/2/2
 **/
@Component
public class ExceptionResolver implements HandlerExceptionResolver{

    private Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);

    @Override
    @ResponseBody
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        String uri = request.getServletPath();
        //打印是哪个请求, 并且打印异常堆栈
        logger.error("{} exception!", uri, e);
        //指定以json格式返回, 而不是返回model 和 view
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("status", ResponseCode.ERROR.getStatus());
        modelAndView.addObject("msg", "请求接口异常, 请在后台查看日志排除bug!");
        modelAndView.addObject("data", e.toString());
        return modelAndView;
    }
}
