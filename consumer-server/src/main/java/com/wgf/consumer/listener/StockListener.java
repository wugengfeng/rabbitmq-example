package com.wgf.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.wgf.base.entity.Stock;
import com.wgf.base.mapper.StockMapper;
import com.wgf.consumer.annotation.IdempotentMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 库存消息监听器
 */
@Slf4j
@Component
public class StockListener {

    @Autowired
    StockMapper stockMapper;

    /**
     * 库存扣减监听
     *
     * @param message
     * @param channel
     * @param messageEntity
     * @return 强制返回 消费成功返回true 否则返回 false
     */
    @IdempotentMessage
    @RabbitListener(queues = "${mq.stock.deduction.queue}")
    public void deductionListener(String message, Channel channel, Message messageEntity) throws Exception {
        boolean result = true;
        try {
            Stock stock = JSON.parseObject(message, Stock.class);

            // 库存大于5抛出异常，用于测试
            if (stock.getStockNum() > 5) {
                throw new RuntimeException();
            }

            // 库存扣减
            stockMapper.deduction(stock);
            // ack确认
            channel.basicAck(messageEntity.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            result = false;
            // Nack
            try {
                channel.basicNack(messageEntity.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                log.error("Nack异常", ex);
            }

            // 消费失败需要抛出异常，AOP会进行消息状态处理
            throw e;
        }
    }
}
