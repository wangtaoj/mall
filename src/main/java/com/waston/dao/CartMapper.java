package com.waston.dao;

import com.waston.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    /**
     * 根据商品ID以及用户ID查询购物车记录
     * @param productId
     * @param userId
     * @return
     */
    Cart selectByProductIdAndUserId(@Param("productId")Integer productId, @Param("userId")Integer userId);

    /**
     * 查询该用户的所有购物车记录
     * @param userId
     * @return
     */
    List<Cart> selectByUserId(Integer userId);

    /**
     * 查询该用户未选中购物车里的商品记录个数
     * @param userId
     * @return
     */
    int selectAllChecked(Integer userId);

    /**
     * 删除多个购物车商品记录
     * @param userId 用户id
     * @param productIdList 商品id集合
     * @return
     */
    int deleteByProductIdsAndUserId(@Param("userId")Integer userId, @Param("productIdList")List<String> productIdList);

    /**
     * 获取当前用户购物车里的记录条数
     * @param userId
     * @return
     */
    int selectCountByUserId(Integer userId);

    /**
     * 全选或者全部取消当前用户购物车中商品记录
     * @param userId
     * @param checked
     * @return
     */
    int updateAllChecked(@Param("userId")Integer userId, @Param("checked")Integer checked);

    /**
     * 查询用户所有选中的购物车商品记录
     * @param userId
     * @return
     */
    List<Cart> selectAllCheckedByUserId(Integer userId);
}