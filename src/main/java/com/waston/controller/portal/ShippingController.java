package com.waston.controller.portal;

import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.Shipping;
import com.waston.pojo.User;
import com.waston.service.ShippingService;
import com.waston.utils.CookieUtil;
import com.waston.utils.JsonUtil;
import com.waston.utils.ShardedRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    /**
     * 添加地址接口
     * @param shipping
     * @param request
     * @return
     */
    @RequestMapping(value = "/add.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse addShipping(Shipping shipping, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return shippingService.saveOrUpdateShipping(currentUser.getId(), shipping);
    }

    /**
     * 删除地址接口
     * @param shippingId
     * @param request
     * @return
     */
    @RequestMapping(value = "/del.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse removeShipping(Integer shippingId, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return shippingService.removeShipping(shippingId, currentUser.getId());
    }

    /**
     * 更新地址接口
     * @param shipping
     * @param request
     * @return
     */
    @RequestMapping(value = "/update.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse updateShipping(Shipping shipping, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return shippingService.saveOrUpdateShipping(currentUser.getId(), shipping);
    }

    /**
     * 查看地址详情接口
     * @param shippingId
     * @param request
     * @return
     */
    @RequestMapping(value = "/select.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse detailShipping(Integer shippingId, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return shippingService.getShipping(shippingId, currentUser.getId());
    }

    /**
     * 获取地址列表接口
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/list.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse listShipping(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
                                       HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return shippingService.selectListShipping(pageNum, pageSize, currentUser.getId());
    }

    /**
     * 从redis获取user
     * @param request
     * @return
     */
    private User getUser (HttpServletRequest request) {
        String loginToken = CookieUtil.getSessionKey(request);
        if(loginToken != null) {
            return JsonUtil.jsonToObject(ShardedRedisUtil.get(loginToken), User.class);
        }
        return null;
    }

}
