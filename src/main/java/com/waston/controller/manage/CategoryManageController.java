package com.waston.controller.manage;

import com.waston.common.ServerResponse;
import com.waston.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类接口
     * @param parentId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "/add_category.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value = "parentId", defaultValue = "0")Integer parentId,
                                      String categoryName) {

        return categoryService.addCategory(parentId, categoryName);
    }

    /**
     * 修改分类名称接口
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "/set_category_name.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse updateCategory(Integer categoryId, String categoryName) {
        return categoryService.updateCategory(categoryId, categoryName);
    }

    /**
     * 获取子分类, 不递归进去, 只查找平级的子分类
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/get_category.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        return categoryService.selectListByCategoryId(categoryId);
    }

    @RequestMapping(value = "/get_deep_category.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse getAllCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {

        return categoryService.selectAllChildren(categoryId);
    }

}
