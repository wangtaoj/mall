package com.waston.controller.manage;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.User;
import com.waston.service.CategoryService;
import com.waston.utils.CookieUtil;
import com.waston.utils.JsonUtil;
import com.waston.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

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
     * @param request
     * @return
     */
    @RequestMapping(value = "/add_category.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value = "parentId", defaultValue = "0")Integer parentId,
                                              String categoryName, HttpServletRequest request) {
        ServerResponse response = check(request);
        if(response != null)
            return response;
        return categoryService.addCategory(parentId, categoryName);
    }

    /**
     * 修改分类名称接口
     * @param categoryId
     * @param categoryName
     * @param request
     * @return
     */
    @RequestMapping(value = "/set_category_name.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse updateCategory(Integer categoryId, String categoryName, HttpServletRequest request) {
        ServerResponse response = check(request);
        if(response != null)
            return response;
        return categoryService.updateCategory(categoryId, categoryName);
    }

    /**
     * 获取子分类, 不递归进去, 只查找平级的子分类
     * @param categoryId
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_category.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId
            , HttpServletRequest request) {
        ServerResponse response = check(request);
        if (response != null)
            return response;
        return categoryService.selectListByCategoryId(categoryId);
    }

    @RequestMapping(value = "/get_deep_category.do", produces = "application/json;chaset=utf-8")
    @ResponseBody
    public ServerResponse getAllCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId
            , HttpServletRequest request) {
        ServerResponse response = check(request);
        if (response != null)
            return response;
        return categoryService.selectAllChildren(categoryId);
    }

    /**
     * 检查权限
     * @param request
     * @return
     */
    private ServerResponse check(HttpServletRequest request) {
        User currentUser = getUser(request);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(),"还未登录");
        }
        if(currentUser.getRole() != Consts.ADMIN_ROLE) {
            return ServerResponse.createByError("无权限操作!");
        }
        return ServerResponse.createBySuccess(currentUser);
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
