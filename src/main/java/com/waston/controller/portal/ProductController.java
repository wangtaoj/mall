package com.waston.controller.portal;

import com.waston.common.Consts;
import com.waston.common.ServerResponse;
import com.waston.service.ProductService;
import com.waston.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author wangtao
 * @Date 2018/1/20
 **/
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 前台商品详情接口
     * @param productId
     * @return
     */
    @RequestMapping(value = "/detail.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse productDetail(Integer productId) {
        ServerResponse<ProductDetailVo> response = productService.getDetail(productId);
        if(!response.isSuccess()) {
            return response;
        }
        ProductDetailVo productDetailVo = response.getData();
        if(productDetailVo.getStatus() != Consts.ON_SALE)
            return ServerResponse.createByError("商品已经下架");
        return response;
    }

    @RequestMapping(value = "/list.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return productService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }

}
