package com.waston.task;

import com.waston.service.OrderService;
import com.waston.utils.ShardedRedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 关闭订单的一个定时任务
 * @Author wangtao
 * @Date 2018/2/3
 **/
@Component
public class CloseOrderTask {

    private Logger logger = LoggerFactory.getLogger(CloseOrderTask.class);

    //redis 关闭订单时的锁, 作为key值
    private static final String CLOSE_ORDER_LOCK = "CLOSE_ORDER_LOCK";

    //有效时间1分钟, 毫秒数
    private static final long EXPIRE_TIME = 1000 * 60;

    @Autowired
    private OrderService orderService;

    /*
     * 以5分钟执行一次
     * 但是该项目使用tomcat集群, 只希望时间点上关单一次而不是多台tomcat都去关单
     */
   /* @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrder() {
        logger.info("定时关单任务开启");
        orderService.updateAndCloseOrder(1);
        logger.info("定时关单任务关闭");
    }*/

    /*
     * 改进方案, 利用redis做一个分布式锁
     * 拿到锁的tomcat执行关单操作，否则跳过
     * 弊端: 如果当执行到业务方法，突然tomcat关闭, 那么redis中该数据将永远存在，因为还没有来的及
     * del操作, 那么将会造成一个死锁
     */
   /* @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrder() {
        logger.info("定时关单任务开启");
        if(ShardedRedisUtil.setnx(CLOSE_ORDER_LOCK, System.currentTimeMillis() + "")) {
            closeOrderTemp(1);
        } else {
            logger.info("{}获取锁失败", Thread.currentThread().getName());
        }

        logger.info("定时关单任务关闭");
    }*/

    /**
     *  继续演进,在将锁存进redis中时, 将值存当前的时间戳
     *  当第一次拿锁失败时再尝试去获取锁, 看锁的存的时间戳+一个超时时间与当前时间戳比较
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrderEvolve() {
        logger.info("定时关单任务开启");
        if(ShardedRedisUtil.setnx(CLOSE_ORDER_LOCK, System.currentTimeMillis() + "")) {
            closeOrderTemp(1);
        } else {
            String oldValue = ShardedRedisUtil.get(CLOSE_ORDER_LOCK);
            if(oldValue != null && System.currentTimeMillis() >= EXPIRE_TIME + Long.parseLong(oldValue)) {
                //有效期时间已过, 我有权利获取到锁
                String value = ShardedRedisUtil.getSet(CLOSE_ORDER_LOCK, System.currentTimeMillis() + "");
                //两者值相等, 拿到锁
                if(Objects.equals(oldValue, value)) {
                    closeOrderTemp(1);
                } else {
                    //不相等, 说明有别的线程或者进程进行了修改, 别人抢先了, 获取失败
                    logger.info("{}获取锁失败", Thread.currentThread().getName());
                }
            } else {
                logger.info("{}获取锁失败", Thread.currentThread().getName());
            }

        }
    }

    private void closeOrderTemp(int hour) {
        //设置锁成功, 那么继续设置一个有效期, 防止死锁
        logger.info("{}拿到锁", Thread.currentThread().getName());
        //关单业务操作
        orderService.updateAndCloseOrder(hour);
        //释放锁
        ShardedRedisUtil.del(CLOSE_ORDER_LOCK);
        logger.info("{}释放锁", Thread.currentThread().getName());
    }

}
