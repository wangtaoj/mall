package com.waston.service;

import com.waston.common.ServerResponse;

/**
 * @Author wangtao
 * @Date 2018/1/21
 **/
public interface CartService {

    /**
     * 添加购物车
     * @param productId
     * @param count
     * @param userId
     * @return
     */
    ServerResponse addCart(Integer productId, Integer count, Integer userId);

    /**
     * 修改购物车商品数量
     * @param productId
     * @param count
     * @param userId
     * @return
     */
    ServerResponse updateCart(Integer productId, Integer count, Integer userId);

    /**
     * 移除选中的购物车记录
     * @param productIds
     * @param userId
     * @return
     */
    ServerResponse removeCart(String productIds, Integer userId);

    /**
     * 获取当前用户购物车中所有商品记录
     * @param userId
     * @return
     */
    ServerResponse listCart(Integer userId);

    /**
     * 选中当前用户购物车中某商品记录
     * @param productId
     * @param userId
     * @return
     */
    ServerResponse setCartChecked(Integer productId, Integer userId);

    /**
     * 取消选中当前用户购物车中某商品记录
     * @param productId
     * @param userId
     * @return
     */
    ServerResponse setCartUnChecked(Integer productId, Integer userId);

    /**
     * 获取当前用户购物车里的记录条数
     * @param userId
     * @return
     */
    ServerResponse getCartNumber(Integer userId);

    /**
     * 全选或者全部取消当前用户购物车中商品记录
     * @param userId
     * @param checked
     * @return
     */
    ServerResponse updateAllCartChecked(Integer userId, Integer checked);
}
