package com.waston.controller.portal;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
     * @param session
     * @return
     */
    @RequestMapping(value = "/add.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse addCart(Integer productId, Integer count, HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.addCart(productId, count, currentUser.getId());
    }

    /**
     * 更新购物车商品数量
     * @param productId
     * @param count
     * @param session
     * @return
     */
    @RequestMapping(value = "/update.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse updateCart(Integer productId,Integer count, HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.updateCart(productId, count, currentUser.getId());
    }

    /**
     * 移除购物车商品记录
     * @param productIds 一个由商品id连接而成的字符串, 用逗号分隔
     * @param session
     * @return
     */
    @RequestMapping(value = "/delete_product.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse removeCart(String productIds, HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.removeCart(productIds, currentUser.getId());
    }

    /**
     * 列出购物车所有商品记录
     * @param session
     * @return
     */
    @RequestMapping(value = "/list.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse listCart(HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.listCart(currentUser.getId());

    }

    /**
     * 选中购物车某个商品记录
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "/select.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse selectCart(Integer productId, HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.setCartChecked(productId, currentUser.getId());
    }

    /**
     * 取消选中购物车某个商品记录
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "/un_select.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse unSelectCart(Integer productId, HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.setCartUnChecked(productId, currentUser.getId());
    }

    /**
     * 获取当前用户购物车里商品数量
     * @param session
     * @return
     */
    @RequestMapping(value = "/get_cart_product_count.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse getCartNumber(HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null)
            return ServerResponse.createBySuccess(0);
        return cartService.getCartNumber(currentUser.getId());
    }

    /**
     * 购物车记录全选
     * @param session
     * @return
     */
    @RequestMapping(value = "/select_all.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse selectAllCart(HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.updateAllCartChecked(currentUser.getId(), Consts.CART_CHECK);
    }

    @RequestMapping(value = "/un_select_all.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse unSelectAllCart(HttpSession session) {
        ServerResponse response = checkLogin(session);
        if(response != null)
            return response;
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        return cartService.updateAllCartChecked(currentUser.getId(), Consts.CART_UNCHECK);
    }
    /**
     * 判断登录
     * @param session
     * @return
     */
    private ServerResponse checkLogin(HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(), "还未登录");
        }
        return null;
    }

}
