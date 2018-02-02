package com.waston.controller.manage;

import com.waston.common.ServerResponse;
import com.waston.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author wangtao
 * @Date 2018/1/24
 **/
@Controller
@RequestMapping("manage/order")
public class OrderManageController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse listOrder(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
        return orderService.listOrderByManage(pageNum, pageSize);
    }

    /**
     * 订单详情
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/detail.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse detailOrder(Long orderNo) {
        return orderService.getOrderByManage(orderNo);
    }

    /**
     * 搜索订单
     * @param orderNo
     * @return
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse search(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
                                 Long orderNo) {
        return orderService.search(pageNum, pageSize, orderNo);
    }

    /**
     * 订单发货
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/send_goods.do", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse deliverProduct(Long orderNo) {
        return orderService.updateOrderStatusToSend(orderNo);
    }
}
