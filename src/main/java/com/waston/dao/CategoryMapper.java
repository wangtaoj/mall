package com.waston.dao;

import com.waston.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /**
     * 判断该分类是否存在
     * @param id
     * @return
     */
    int isExist(Integer id);

    /**
     * 获取平级的子分类
     * @param parentId
     * @return
     */
    List<Category> selectListByCategoryId(Integer parentId);
}