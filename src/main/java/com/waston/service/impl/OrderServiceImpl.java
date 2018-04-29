package com.waston.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.waston.common.Consts;
import com.waston.common.ServerResponse;
import com.waston.dao.*;
import com.waston.pojo.*;
import com.waston.service.OrderService;
import com.waston.utils.BigDecimalUtil;
import com.waston.utils.DateUtil;
import com.waston.utils.FTPUtil;
import com.waston.utils.PropertiesUtil;
import com.waston.vo.OrderItemVo;
import com.waston.vo.OrderProductVo;
import com.waston.vo.OrderVo;
import com.waston.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;
    static {
        /*
         * 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         * Configs会读取classpath下的alipay.properties文件配置信息,
         * 如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("alipay.properties");

        /*
         * 使用Configs提供的默认参数
         * AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse addOrder(Integer userId, Integer shippingId) {
        if(shippingId == null) {
            return ServerResponse.createByError("请选择收获地址再提交");
        }
        //根据用户勾选的购物车信息组装OrderItem
        List<Cart> carts = cartMapper.selectAllCheckedByUserId(userId);
        Long orderNo = generateOrderNo();
        if(carts == null || carts.isEmpty())
            return ServerResponse.createByError("请选择购物车里的商品后再提交");
        ServerResponse<List<OrderItem>> response = getOrderItems(carts, orderNo);
        if(!response.isSuccess())
            return response;
        List<OrderItem> orderItems = response.getData();
        //计算订单总价
        BigDecimal payment = calPayment(orderItems);

        //插入订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(Consts.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPostage(0);
        order.setStatus(Consts.OrderStatusEnum.NO_PAY.getCode());
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        if(orderMapper.insert(order) <= 0) {
            return ServerResponse.createByError("创建订单失败");
        }
        //批量插入订单明细
        orderItemMapper.insertBatch(orderItems);

        //减少库存
        reduceProductStock(orderItems);

        //清空购物车
        cleanCart(carts);
        //组装返回数据
        OrderVo orderVo = buildOrderVo(order, orderItems);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中获取数据

        List<Cart> cartList = cartMapper.selectAllCheckedByUserId(userId);
        ServerResponse<List<OrderItem>> serverResponse =  this.getOrderItems(cartList, null);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = serverResponse.getData();

        List<OrderItemVo> orderItemVoList = new ArrayList<>();

        BigDecimal payment = BigDecimal.ZERO;
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerResponse listOrder(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVos = new ArrayList<>();
        for(Order order : orders) {
            List<OrderItem> orderItems = orderItemMapper.selectListByOrderNoAndUserId(order.getOrderNo(), order.getUserId());
            OrderVo orderVo = buildOrderVo(order, orderItems);
            orderVos.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo<>(orders);
        pageInfo.setList(orderVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse getOrder(Long orderNo, Integer userId) {
        if(orderNo == null) {
            return ServerResponse.createByError("参数错误, 请选择一个订单");
        }
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(order == null) {
            return ServerResponse.createByError("没有该订单");
        }
        List<OrderItem> orderItems = orderItemMapper.selectListByOrderNoAndUserId(orderNo, userId);
        OrderVo orderVo = buildOrderVo(order, orderItems);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse updateOrderStatus(Long orderNo, Integer userId) {
        if(orderNo == null) {
            return ServerResponse.createByError("参数错误, 请选择一个订单");
        }
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(order == null) {
            return ServerResponse.createByError("没有该订单");
        }
        if(order.getStatus() != Consts.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByError("该订单已经取消过了或者已经付款了");
        }

        //把商品库存增加
        List<OrderItem> orderItems = orderItemMapper.selectListByOrderNoAndUserId(orderNo, userId);
        addProductStock(orderItems);

        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Consts.OrderStatusEnum.CANCELED.getCode());
        newOrder.setUpdateTime(new Date());
        if(orderMapper.updateByPrimaryKeySelective(newOrder) > 0) {
            return ServerResponse.createBySuccessMsg("取消订单成功");
        }
        return ServerResponse.createByError("取消订单失败");
    }

    public ServerResponse updateOrderStatusToSend(Long orderNo) {
        if(orderNo == null) {
            return ServerResponse.createByError("参数错误, 请选择一个订单");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null) {
            return ServerResponse.createByError("没有该订单");
        }
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Consts.OrderStatusEnum.SHIPPED.getCode());
        newOrder.setUpdateTime(new Date());
        if(orderMapper.updateByPrimaryKeySelective(newOrder) > 0) {
            return ServerResponse.createBySuccessMsg("设置订单发货成功");
        }
        return ServerResponse.createByError("设置订单发货失败");
    }

    //manage
    @SuppressWarnings("unchecked")
    @Override
    public ServerResponse listOrderByManage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<Order> orders = orderMapper.selectAll();
        List<OrderVo> orderVos = new ArrayList<>();
        for(Order order : orders) {
            List<OrderItem> orderItems = orderItemMapper.selectListByOrderNo(order.getOrderNo());
            OrderVo orderVo = buildOrderVo(order, orderItems);
            orderVos.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo<>(orders);
        pageInfo.setList(orderVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse getOrderByManage(Long orderNo) {
        if(orderNo == null) {
            return ServerResponse.createByError("参数错误, 请选择一个订单");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null) {
            return ServerResponse.createByError("没有该订单");
        }
        List<OrderItem> orderItems = orderItemMapper.selectListByOrderNo(orderNo);
        OrderVo orderVo = buildOrderVo(order, orderItems);
        return ServerResponse.createBySuccess(orderVo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerResponse<PageInfo> search(int pageNum,int pageSize, Long orderNo){
        PageHelper.startPage(pageNum,pageSize, "create_time desc");
        List<Order> orders = orderMapper.selectAllByOrderNo("%" + orderNo + "%");
        if(orders == null || orders.isEmpty()) {
            return ServerResponse.createByError("没有搜索到订单");
        }
        PageInfo pageResult = new PageInfo<>(orders);
        List<OrderVo> orderVos = new ArrayList<>();
        for(Order order : orders){
            List<OrderItem> orderItemList = orderItemMapper.selectListByOrderNo(order.getOrderNo());
            OrderVo orderVo = buildOrderVo(order,orderItemList);
            orderVos.add(orderVo);
        }
        pageResult.setList(orderVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 生成订单号
     * @return
     */
    private Long generateOrderNo() {
        return Long.valueOf(System.currentTimeMillis() + "" + new Random().nextInt(100));
    }

    /**
     * 根据购物车信息以及订单号获取订单明细列表
     * @param carts
     * @return
     */
    private ServerResponse<List<OrderItem>> getOrderItems(List<Cart> carts, Long orderNo) {
        List<OrderItem> orderItems = new ArrayList<>();
        for(Cart cart : carts) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //检验商品状态
            if(product.getStatus() != Consts.ON_SALE) {
                return ServerResponse.createByError(product.getName() + "商品已经不在售卖状态");
            }
            //检验商品库存
            if(product.getStock() < cart.getQuantity()) {
                return ServerResponse.createByError(product.getName() + "商品库存不足");
            }
            orderItem.setUserId(cart.getUserId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(cart.getQuantity(), product.getPrice().doubleValue()));
            Date date = new Date();
            orderItem.setCreateTime(date);
            orderItem.setUpdateTime(date);
            orderItems.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItems);
    }

    /**
     * 计算订单总价格
     * @param orderItems
     * @return
     */
    private BigDecimal calPayment(List<OrderItem> orderItems) {
        BigDecimal ans = BigDecimal.ZERO;
        for(OrderItem orderItem : orderItems) {
            ans = BigDecimalUtil.add(ans.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return ans;
    }

    /**
     * 创建订单时, 减少商品库存
     * @param orderItems
     */
    private void reduceProductStock(List<OrderItem> orderItems){
        for(OrderItem orderItem : orderItems){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * 取消订单时, 恢复商品库存
     * @param orderItems
     */
    private void addProductStock(List<OrderItem> orderItems) {
        for(OrderItem orderItem : orderItems){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() + orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * 清空购物车
     * @param cartList
     */
    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * 组装数据返回
     * @param order
     * @param orderItemList
     * @return
     */
    private OrderVo buildOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        Consts.PaymentTypeEnum paymentTypeEnum = Consts.PaymentTypeEnum.valueOf(order.getPaymentType());
        orderVo.setPaymentTypeDesc(paymentTypeEnum == null ? null : paymentTypeEnum.getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        Consts.OrderStatusEnum orderStatusEnum = Consts.OrderStatusEnum.valueOf(order.getStatus());
        orderVo.setStatusDesc(orderStatusEnum == null ? null : orderStatusEnum.getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateUtil.toString(order.getPaymentTime()));
        orderVo.setSendTime(DateUtil.toString(order.getSendTime()));
        orderVo.setEndTime(DateUtil.toString(order.getEndTime()));
        orderVo.setCreateTime(DateUtil.toString(order.getCreateTime()));
        orderVo.setCloseTime(DateUtil.toString(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = new ArrayList<>();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateUtil.toString(orderItem.getCreateTime()));
        return orderItemVo;
    }



    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        if(orderNo == null)
            return ServerResponse.createByError("参数错误");
        //查询订单
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(order == null) {
            return ServerResponse.createByError("用户没有此订单号");
        }
        if(order.getStatus() == Consts.OrderStatusEnum.CANCELED.getCode()) {
            return ServerResponse.createByError("订单已经被取消了");
        }

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();


        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "happymmall扫码支付,订单号:" + outTradeNo;


        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();


        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";



        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单" + outTradeNo + "购买商品共" + totalAmount + "元";


        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");




        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();

        List<OrderItem> orderItemList = orderItemMapper.selectListByOrderNoAndUserId(orderNo,userId);
        for(OrderItem orderItem : orderItemList){
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(),100D).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                // 需要修改为运行机器上的路径
                //细节细节细节
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                logger.info("filePath:" + qrPath);

                File targetFile = new File(path,qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常",e);
                }
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("orderNo", order.getOrderNo());
                resultMap.put("qrPath",PropertiesUtil.getProperty("ftp.server.http.prefix") + qrFileName);
                return ServerResponse.createBySuccess(resultMap);


            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByError("支付宝预下单失败!!!");


            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByError("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByError("不支持的交易状态，交易返回异常!!!");

        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse aliCallback(Map<String,String> params) {
        //订单号
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        //支付宝交易号
        String tradeNo = params.get("trade_no");
        //此订单目前的交易状态(四个状态, 支付宝官方提供, 已经声明为)
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByError("不是此商城的订单,回调忽略");
        }
        //已经支付过了, 给Controller正确的返回, Controller层判断正确则返回支付宝success
        if (order.getStatus() >= Consts.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        //交易成功
        if (Consts.TRADE_STATUS.TRADE_SUCCESS.equals(tradeStatus)) {
            //更新订单状态以及支付时间
            order.setPaymentTime(DateUtil.parseToDate(params.get("gmt_payment")));
            order.setStatus(Consts.OrderStatusEnum.PAID.getCode());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        //插入支付信息
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Consts.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        Date date = new Date();
        payInfo.setCreateTime(date);
        payInfo.setUpdateTime(date);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo){
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(order == null){
            return ServerResponse.createByError("用户没有该订单");
        }
        if(order.getStatus() >= Consts.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
