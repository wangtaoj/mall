package com.waston.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangtao
 * Created on 2018/3/13
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    /**
     * 测试是否会出现超卖情况
     * @throws Exception
     */
    @Test
    public void updateTestReduceStock() throws Exception {

        ExecutorService service = Executors.newCachedThreadPool();
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        //模拟1000个人去抢购6号商品, 库存为10
        for(int i = 1; i <= 1000; i++) {
            service.execute(()->orderService.updateTestReduceStock(success, fail));
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        System.out.println("-----------------------------------");
        System.out.println("success: " + success.get());
        System.out.println("fail: " + fail.get());
        System.out.println("-----------------------------------");
    }
}