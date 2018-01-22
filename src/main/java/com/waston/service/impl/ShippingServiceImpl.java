package com.waston.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.waston.common.ServerResponse;
import com.waston.dao.ShippingMapper;
import com.waston.pojo.Shipping;
import com.waston.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse saveOrUpdateShipping(Integer userId, Shipping shipping) {
        if(shipping == null) {
            return ServerResponse.createByError("参数错误");
        }
        shipping.setUserId(userId);
        Date date = new Date();
        shipping.setUpdateTime(date);
        if(shipping.getId() == null) {
            shipping.setCreateTime(date);
            if(shippingMapper.insert(shipping) > 0) {
                Map<String, Integer> map = new LinkedHashMap<>();
                map.put("shipingId", shipping.getId());
                return ServerResponse.createBySuccess("新建地址成功", map);
            }
            return ServerResponse.createByError("新建地址失败");
        } else {
            if(shippingMapper.updateByPrimaryKeyAndUserId(shipping) > 0) {
                return ServerResponse.createBySuccessMsg("更新地址成功");
            }
            return ServerResponse.createByError("更新地址失败");
        }

    }

    @Override
    public ServerResponse removeShipping(Integer shippingId, Integer userId) {
        if(shippingId == null) {
            return ServerResponse.createByError("参数错误");
        }
        if(shippingMapper.deleteByPrimaryKeyAndUserId(shippingId, userId) > 0) {
            return ServerResponse.createBySuccessMsg("删除地址成功");
        }
        return ServerResponse.createByError("删除地址失败");
    }

    @Override
    public ServerResponse getShipping(Integer shippingId, Integer userId) {
        if(shippingId == null) {
            return ServerResponse.createByError("参数错误");
        }
        return ServerResponse.createBySuccess(shippingMapper.selectByPrimaryKey(shippingId));
    }

    @Override
    public ServerResponse selectListShipping(int pageNum, int pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByPage(userId);
        PageInfo pageInfo = new PageInfo<>(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
