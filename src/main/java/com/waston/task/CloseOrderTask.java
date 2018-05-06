package com.waston.task;

import com.waston.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时关单
 *
 * @author wangtao
 * Created on 2018/5/6
 **/

@Component
public class CloseOrderTask {

    private Logger logger = LoggerFactory.getLogger(CloseOrderTask.class);

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    @Autowired
    private OrderService orderService;

    public CloseOrderTask() {
        colseOrder();
    }

    public void colseOrder() {
        service.scheduleAtFixedRate(() -> {
            logger.info("开始关单");
            orderService.updateAndCloseOrder(1);
            logger.info("关单完成");
        }, 2, 2, TimeUnit.MINUTES);
    }

}
