package com.waston.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.waston.common.Consts;
import com.waston.common.ServerResponse;
import com.waston.dao.CategoryMapper;
import com.waston.dao.ProductMapper;
import com.waston.pojo.Category;
import com.waston.pojo.Product;
import com.waston.service.CategoryService;
import com.waston.service.ProductService;
import com.waston.vo.ProductDetailVo;
import com.waston.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {
        if(product != null) {
            //sumImages存取多个图片地址,用逗号分隔
            if(!StringUtils.isEmpty(product.getSubImages())) {
                String[] res = product.getSubImages().split(",");
                if(res.length > 0) {
                    //主图
                    product.setMainImage(res[0]);
                }
            }
            Date date = new Date();
            product.setUpdateTime(date);
            //新增
            if(product.getId() == null) {
                product.setCreateTime(date);
                product.setStatus(1);
                if(productMapper.insert(product) > 0)
                    return ServerResponse.createBySuccessMsg("新增商品成功!");
                return ServerResponse.createByError("新增商品失败");
            } else {
                //修改
                if(productMapper.updateByPrimaryKeySelective(product) > 0)
                    return ServerResponse.createBySuccessMsg("修改商品成功!");
                return ServerResponse.createByError("修改商品失败");
            }

        }
        return ServerResponse.createByError("商品参数错误!");
    }

    @Override
    public ServerResponse<String> updateProductStatus(Integer productId, Integer status) {
        if(StringUtils.isEmpty(productId) || StringUtils.isEmpty(status))
            return ServerResponse.createByError("参数错误");
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        product.setUpdateTime(new Date());
        if(productMapper.updateByPrimaryKeySelective(product) > 0)
            return ServerResponse.createBySuccessMsg("商品状态更新成功");
        return ServerResponse.createByError("商品状态更新失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        if(StringUtils.isEmpty(productId))
            return ServerResponse.createByError("参数错误");
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null)
            return ServerResponse.createByError("该商品不存在");
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        Integer categoryParentId = 0;//根节点
        String categoryName = "该商品没有分类, 野生??";
        if(category != null) {
            categoryParentId = category.getParentId();
            categoryName = category.getName();
        }
        //封装商品详情返回
        ProductDetailVo productDetailVo = ProductDetailVo.buildProductDetailVo(product, categoryParentId, categoryName);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<PageInfo> getList(int pageNum, int pageSize) {
        //开始分页, 设置参数
        PageHelper.startPage(pageNum,pageSize);
        //分页查询
        List<Product> products = productMapper.selectByPage();
        List<ProductListVo> productListVos = new ArrayList<>();
        for(Product product : products){
            ProductListVo productListVo = ProductListVo.buildProductListVO(product);
            productListVos.add(productListVo);
        }
        //封装结果
        PageInfo pageResult = new PageInfo<>(products);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        //开始分页
        PageHelper.startPage(pageNum,pageSize);
        if(!StringUtils.isEmpty(productName)){
            productName = "%" + productName + "%";
        }
        //根据所给条件查询
        List<Product> products = productMapper.selectByNameAndId(productId, productName);
        List<ProductListVo> productListVos = new ArrayList<>();
        for(Product product : products){
            ProductListVo productListVo = ProductListVo.buildProductListVO(product);
            productListVos.add(productListVo);
        }
        //封装结果
        PageInfo pageResult = new PageInfo<>(products);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        //两者为空, 参数错误
        if(StringUtils.isEmpty(keyword) && categoryId == null)
            return ServerResponse.createByError("参数错误");
        List<Integer> categories = null;
        if(categoryId != null) {
            categories = categoryService.selectAllChildren(categoryId).getData();
        }
        if(!StringUtils.isEmpty(keyword)) {
            keyword = "%" + keyword + "%";
        }
        //开始进行分页查询, 并且根据排序规则进行排序
        PageHelper.startPage(pageNum, pageSize);
        //如果需要排序
        if(Consts.ORDER_SET.contains(orderBy)) {
            String[] orderArray = orderBy.split("_");
            PageHelper.orderBy(orderArray[0] + " " + orderArray[1]); //参数: '字段 desc/asc'
        }
        List<Product> products = productMapper.selectByNameOrCategoryIds(keyword, categories);
        List<ProductListVo> productListVos = new ArrayList<>();
        for(Product product : products){
            ProductListVo productListVo = ProductListVo.buildProductListVO(product);
            productListVos.add(productListVo);
        }
        //封装结果
        PageInfo pageResult = new PageInfo<>(products);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }
}
