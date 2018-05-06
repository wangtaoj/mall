package com.waston.dao;

import com.waston.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * 商品是否存在
     * @param productId
     * @return
     */
    int isExist(Integer productId);

    /**
     * 分页查询商品
     * @return
     */
    List<Product> selectByPage();

    /**
     * 根据id, name的值动态查询
     * @param productId
     * @param productName
     * @return
     */
    List<Product> selectByNameAndId(@Param("productId")Integer productId, @Param("productName")String productName);

    /**
     * 根据商品名字和分类id集合动态查询商品
     * @param productName
     * @param categories
     * @return
     */
    List<Product> selectByNameOrCategoryIds(@Param("productName")String productName, @Param("categories")List<Integer> categories);


    /**
     * 减库存
     * @param productId
     * @param number
     * @return
     */
    int reduceStock(@Param("productId")Integer productId, @Param("number")int number);

}