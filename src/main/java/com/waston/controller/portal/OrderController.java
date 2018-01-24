package com.waston.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
@Controller
@RequestMapping("/order")
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 支付接口
     * @param orderNo
     * @param session
     * @return
     */
    @RequestMapping(value = "/pay.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse pay(Long orderNo, HttpSession session) {
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        String path = session.getServletContext().getRealPath("/upload");
        return orderService.pay(orderNo, user.getId(), path);
    }

    /**
     * 支付宝回调接口
     * @param request
     * @return
     */
    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByError("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }

        ServerResponse serverResponse = orderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            logger.info("回调完成, 结果为true");
            return "success";
        }
        logger.info("回调完成, 结果为false");
        return "fail";
    }

    /**
     * 查询订单状态
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(),"还未登录");
        }
        ServerResponse serverResponse = orderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    /**
     * 用户创建订单接口
     * @param shippingId 地址ID
     * @param session
     * @return
     */
    @RequestMapping(value = "/create.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse addOrder(Integer shippingId, HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return orderService.addOrder(currentUser.getId(), shippingId);
    }

    /**
     * 获取购物车里的商品信息
     * @param session
     * @return
     */
    @RequestMapping(value = "/get_order_cart_product.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User currentUser = (User) session.getAttribute(Consts.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return orderService.getOrderCartProduct(currentUser.getId());
    }

    /**
     * 列出所有订单
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "/list.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse listOrder(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize, HttpSession session) {
        User currentUser = (User) session.getAttribute(Consts.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return orderService.listOrder(pageNum, pageSize, currentUser.getId());
    }

    /**
     * 获取订单详情
     * @param orderNo
     * @param session
     * @return
     */
    @RequestMapping(value = "/detail.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse detailOrder(Long orderNo, HttpSession session) {
        User currentUser = (User) session.getAttribute(Consts.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return orderService.getOrder(orderNo, currentUser.getId());
    }

    /**
     * 取消订单
     * @param orderNo
     * @param session
     * @return
     */
    @RequestMapping(value = "/cancel.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse cancelOrder(Long orderNo, HttpSession session) {
        User currentUser = (User) session.getAttribute(Consts.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return orderService.updateOrderStatus(orderNo, currentUser.getId());
    }
}
