package com.waston.service.impl;

import com.waston.common.ServerResponse;
import com.waston.dao.CategoryMapper;
import com.waston.pojo.Category;
import com.waston.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        if(parentId == null || StringUtils.isEmpty(categoryName))
            return ServerResponse.createByError("参数错误");
        //检查parentId是否存在
        if(categoryMapper.isExist(parentId) <= 0 && parentId != 0)
            return ServerResponse.createByError("父分类不存在, 请重新选取");
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); //可用
        Date date = new Date();
        category.setCreateTime(date);
        category.setUpdateTime(date);
        if(categoryMapper.insertSelective(category) > 0)
            return ServerResponse.createBySuccessMsg("商品类别添加成功");
        return ServerResponse.createByError("类别添加失败");
    }

    @Override
    public ServerResponse<String> updateCategory(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isEmpty(categoryName))
            return ServerResponse.createByError("参数错误");
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null)
            return ServerResponse.createByError("该分类不存在");
        category.setId(categoryId);
        category.setName(categoryName);
        Date date = new Date();
        category.setUpdateTime(date);
        if(categoryMapper.updateByPrimaryKeySelective(category) > 0)
            return ServerResponse.createBySuccessMsg("更新品类名字成功");
        return ServerResponse.createByError("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Category>> selectListByCategoryId(Integer categoryId) {
        List<Category> categories = categoryMapper.selectListByCategoryId(categoryId);
        if(categories == null || categories.isEmpty())
            return ServerResponse.createByError("该分类没有子分类");
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public ServerResponse<List<Integer>> selectAllChildren(Integer categoryId) {
        Set<Category> set = new HashSet<>();
        dfs(set, categoryId);
        if(set.isEmpty())
            return ServerResponse.createByError("该分类没有子分类");
        List<Integer> list = new ArrayList<>();
        for(Category category : set) {
            list.add(category.getId());
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     * 递归查找子节点
     * @param set
     * @param categoryId
     */
    private void dfs(Set<Category> set, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null) {
            set.add(category);
        }
        if(category != null || categoryId == 0) {
            List<Category> categories = categoryMapper.selectListByCategoryId(categoryId);
            for(Category c : categories) {
                dfs(set, c.getId());
            }
        }
    }
}
