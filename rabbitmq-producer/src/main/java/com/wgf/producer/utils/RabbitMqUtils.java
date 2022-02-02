package com.wgf.producer.utils;

import com.wgf.base.constant.MsgStatus;
import com.wgf.base.entity.MsgLog;
import com.wgf.producer.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class RabbitMqUtils {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MsgLogService msgLogService;


    /**
     * 发送消息
     *
     * @param exchangeName 交换机
     * @param routingKey   路由键
     * @param msg          消息
     * @return 消息唯一id
     */
    public Long sendMsg(MsgLog msgLog) {
        if (Objects.isNull(msgLog.getMsgId())) {
            throw new RuntimeException("msgId 为空");
        }

        try {
            // 消息持久化
            this.msgLogService.saveMsg(msgLog);
            this.rabbitTemplate.convertAndSend(msgLog.getExchangeName(), msgLog.getRoutingKey(), msgLog.getMsg(), correlationData -> {
                String messageId = String.format("%s_%s", msgLog.getId(), msgLog.getMsgId());
                correlationData.getMessageProperties().setMessageId(messageId);
                return correlationData;
            });
        } catch (Exception e) {
            log.error("消息发送异常", e);
            msgLog.setMsgStatus(MsgStatus.FAIL.getCode());
            this.msgLogService.saveOrUpdate(msgLog);
        }

        return msgLog.getMsgId();
    }
}
