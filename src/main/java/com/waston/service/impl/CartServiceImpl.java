package com.waston.service.impl;

import com.google.common.collect.Lists;
import com.waston.common.Consts;
import com.waston.common.ServerResponse;
import com.waston.dao.CartMapper;
import com.waston.dao.ProductMapper;
import com.waston.pojo.Cart;
import com.waston.pojo.Product;
import com.waston.service.CartService;
import com.waston.utils.BigDecimalUtil;
import com.waston.utils.PropertiesUtil;
import com.waston.vo.CartProductVo;
import com.waston.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author wangtao
 * @Date 2018/1/21
 **/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse addCart(Integer productId, Integer count, Integer userId) {
        //检查参数
        if(productId == null || count == null || count <= 0)
            return ServerResponse.createByError("参数错误");
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null)
            return ServerResponse.createByError("该商品不存在");
        if(product.getStock() < count)
            return ServerResponse.createByError("商品库存不足");
        //先查询是否有该记录
        Cart cart = cartMapper.selectByProductIdAndUserId(productId, userId);
        if(cart == null) {
            //等于null, 插入记录
            cart = new Cart();
            cart.setChecked(Consts.CART_CHECK);
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(count);
            Date date = new Date();
            cart.setCreateTime(date);
            cart.setUpdateTime(date);
            if(cartMapper.insert(cart) > 0)
                return ServerResponse.createBySuccess(buildResult(userId));
            else
                return ServerResponse.createByError("购物车添加失败");
        } else {
            //用户又选择了同一个商品加入购物车, 改变记录的数目quantity = quantity + count;
            cart.setQuantity(count + cart.getQuantity());
            cart.setUpdateTime(new Date());
            if(cartMapper.updateByPrimaryKey(cart) > 0)
                return ServerResponse.createBySuccess(buildResult(userId));
            else
                return ServerResponse.createByError("购物车添加失败");
        }
    }

    @Override
    public ServerResponse updateCart(Integer productId, Integer count, Integer userId) {
        //检查参数
        if(productId == null || count == null)
            return ServerResponse.createByError("参数错误");
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByError("此商品不存在");
        }
        if(product.getStock() < count) {
            return ServerResponse.createByError("库存不足");
        }
        Cart cart = cartMapper.selectByProductIdAndUserId(productId, userId);
        if(cart == null) {
            return ServerResponse.createByError("该购物车记录不存在");
        }
        if(count == 0) {
            if(cartMapper.deleteByPrimaryKey(cart.getId()) > 0)
                return ServerResponse.createBySuccess(buildResult(userId));
            return ServerResponse.createByError("购物车记录更新失败");
        } else {
            cart.setUpdateTime(new Date());
            cart.setQuantity(count);
            if(cartMapper.updateByPrimaryKeySelective(cart) > 0)
                return ServerResponse.createBySuccess(buildResult(userId));
            return ServerResponse.createByError("购物车记录更新失败");
        }

    }

    @Override
    public ServerResponse removeCart(String productIds, Integer userId) {
        if(StringUtils.isEmpty(productIds))
            return ServerResponse.createByError("参数错误");
        String[] productId = productIds.split(",");
        List<String> productIdList = Lists.newArrayList(productId);
        if(cartMapper.deleteByProductIdsAndUserId(userId, productIdList) > 0) {
            return ServerResponse.createBySuccess(buildResult(userId));
        }
        return ServerResponse.createByError("删除购物车商品记录失败");
    }

    @Override
    public ServerResponse listCart(Integer userId) {
        return ServerResponse.createBySuccess(buildResult(userId));
    }

    @Override
    public ServerResponse setCartChecked(Integer productId, Integer userId) {
        return updateCartChecked(productId, userId, Consts.CART_CHECK);
    }

    @Override
    public ServerResponse setCartUnChecked(Integer productId, Integer userId) {
        return updateCartChecked(productId, userId, Consts.CART_UNCHECK);
    }

    @Override
    public ServerResponse getCartNumber(Integer userId) {
        return ServerResponse.createBySuccess(cartMapper.selectCountByUserId(userId));
    }

    @Override
    public ServerResponse updateAllCartChecked(Integer userId, Integer checked) {
        cartMapper.updateAllChecked(userId, checked);
        return ServerResponse.createBySuccess(buildResult(userId));
    }

    private ServerResponse updateCartChecked(Integer productId, Integer userId, Integer checked) {
        if(productId == null)
            return ServerResponse.createByError("参数错误");
        Cart cart = cartMapper.selectByProductIdAndUserId(productId, userId);
        if(cart == null) {
            return ServerResponse.createByError("该购物车商品记录不存在");
        }
        Cart newCart = new Cart();
        newCart.setId(cart.getId());
        newCart.setUpdateTime(new Date());
        newCart.setChecked(checked);
        if(cartMapper.updateByPrimaryKeySelective(newCart) > 0)
            return ServerResponse.createBySuccess(buildResult(userId));
        return ServerResponse.createByError("勾选商品记录失败");
    }

    /**
     * 构建结果返回
     * @param userId
     * @return
     */
    private CartVo buildResult(Integer userId) {

        //封装数据
        CartVo cartVo = new CartVo();
        List<Cart> carts = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        for(Cart cart : carts) {
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(cart.getUserId());
            cartProductVo.setProductId(cart.getProductId());
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(product != null) {
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStock(product.getStock());
                int quantity;
                //判断库存
                if(product.getStock() >= cart.getQuantity()) {
                    quantity = cart.getQuantity();
                    cartProductVo.setLimitQuantity(Consts.CART_SUCCESS);
                } else {
                    //库存不够, 将真实的商品数量更新到购物车记录中
                    quantity = product.getStock();
                    cartProductVo.setLimitQuantity(Consts.CART_FAIL);
                    Cart cartNew = new Cart();
                    cartNew.setId(cart.getId());
                    cartNew.setQuantity(quantity);
                    cartNew.setUpdateTime(new Date());
                    cartMapper.updateByPrimaryKeySelective(cartNew);
                }
                cartProductVo.setQuantity(quantity);
                //计算该记录的总价格
                cartProductVo.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), 1.0 * quantity));
            }
            cartProductVo.setProductChecked(cart.getChecked());
            cartProductVoList.add(cartProductVo);
            if(Objects.equals(cart.getChecked(), Consts.CART_CHECK)) {
                //计算整个购物车的总价格, 需要是选中的商品才累加
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(cartMapper.selectAllChecked(userId) == 0);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }
}
