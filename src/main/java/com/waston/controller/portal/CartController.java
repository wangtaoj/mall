package com.waston.controller.portal;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.CartService;
import com.waston.utils.CookieUtil;
import com.waston.utils.JsonUtil;
import com.waston.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author wangtao
 * @Date 2018/1/21
 **/
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车接口
     * @param productId
     * @param count
     * @param request
     * @return
     */
    @RequestMapping(value = "/add.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse addCart(Integer productId, Integer count, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.addCart(productId, count, currentUser.getId());
    }

    /**
     * 更新购物车商品数量
     * @param productId
     * @param count
     * @param request
     * @return
     */
    @RequestMapping(value = "/update.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse updateCart(Integer productId,Integer count, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.updateCart(productId, count, currentUser.getId());
    }

    /**
     * 移除购物车商品记录
     * @param productIds 一个由商品id连接而成的字符串, 用逗号分隔
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete_product.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse removeCart(String productIds, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.removeCart(productIds, currentUser.getId());
    }

    /**
     * 列出购物车所有商品记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/list.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse listCart(HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.listCart(currentUser.getId());

    }

    /**
     * 选中购物车某个商品记录
     * @param productId
     * @param request
     * @return
     */
    @RequestMapping(value = "/select.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse selectCart(Integer productId, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.setCartChecked(productId, currentUser.getId());
    }

    /**
     * 取消选中购物车某个商品记录
     * @param productId
     * @param request
     * @return
     */
    @RequestMapping(value = "/un_select.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse unSelectCart(Integer productId, HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.setCartUnChecked(productId, currentUser.getId());
    }

    /**
     * 获取当前用户购物车里商品数量
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_cart_product_count.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse getCartNumber(HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null)
            return ServerResponse.createBySuccess(0);
        return cartService.getCartNumber(currentUser.getId());
    }

    /**
     * 购物车记录全选
     * @param request
     * @return
     */
    @RequestMapping(value = "/select_all.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse selectAllCart(HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.updateAllCartChecked(currentUser.getId(), Consts.CART_CHECK);
    }

    /**
     * 取消购物车记录全选
     * @param request
     * @return
     */
    @RequestMapping(value = "/un_select_all.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse unSelectAllCart(HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return cartService.updateAllCartChecked(currentUser.getId(), Consts.CART_UNCHECK);
    }

    /**
     * 从redis获取user
     * @param request
     * @return
     */
    private User getUser (HttpServletRequest request) {
        String loginToken = CookieUtil.getSessionKey(request);
        if(loginToken != null) {
            return JsonUtil.jsonToObject(RedisUtil.get(loginToken), User.class);
        }
        return null;
    }

}
