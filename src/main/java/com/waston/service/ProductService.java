package com.waston.service;

import com.github.pagehelper.PageInfo;
import com.waston.common.ServerResponse;
import com.waston.pojo.Product;
import com.waston.vo.ProductDetailVo;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
public interface ProductService {

    /**
     * 添加或者更新商品信息
     * @param product
     * @return
     */
    ServerResponse<String> saveOrUpdateProduct(Product product);

    /**
     * 更新商品状态
     * @param productId
     * @param status
     * @return
     */
    ServerResponse<String> updateProductStatus(Integer productId, Integer status);

    /**
     * 获取商品详情
     * @param productId
     * @return
     */
    ServerResponse<ProductDetailVo> getDetail(Integer productId);

    /**
     * 分页获取商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> getList(int pageNum, int pageSize);

    /**
     * 搜索商品, 根据商品名字或者id动态的搜索
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);


    /**
     * 根据关键字或者商品分类ID搜索商品, 并且根据排序规则将结果进行排序
     * @param keyword 搜索关键字
     * @param categoryId 分类ID
     * @param pageNum
     * @param pageSize
     * @param orderBy 排序规则 price_desc, price_asc
     * @return
     */
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
