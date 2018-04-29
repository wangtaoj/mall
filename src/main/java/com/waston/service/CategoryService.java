package com.waston.service;

import com.waston.common.ServerResponse;
import com.waston.pojo.Category;

import java.util.List;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
public interface CategoryService {

    /**
     * 添加分类
     * @param parentId
     * @param categoryName
     * @return
     */
    ServerResponse<String> addCategory(Integer parentId, String categoryName);

    /**
     * 修改分类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse<String> updateCategory(Integer categoryId, String categoryName);

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    ServerResponse removeCategory(Integer categoryId);

    /**
     * 获取子分类, 平级
     * @param categoryId
     * @return
     */
    ServerResponse<List<Category>> selectListByCategoryId(Integer categoryId);

    /**
     * 递归获取所有子分类, 包括本身
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> selectAllChildren(Integer categoryId);

}
