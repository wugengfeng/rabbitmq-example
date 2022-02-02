package com.wgf.producer.mq;

import com.wgf.producer.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 可以确认消息从EXchange路由到Queue失败
 * 如果正常投递到队列，不会回调ReturnCallback
 */
@Slf4j
@Component
public class MsgReturnCallback implements RabbitTemplate.ReturnCallback {
    /**
     * 回调标记
     */
    public final static int FLAG = 1;

    @Autowired
    MsgLogService msgLogService;

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.debug("ReturnCallback unroutable messages, message = {} , replyCode = {} , replyText = {} , exchange = {} , routingKey = {} ", message, replyCode, replyText, exchange, routingKey);

        String   messageId = message.getMessageProperties().getMessageId();
        String[] arr       = messageId.split("_");
        Long     logId     = Long.valueOf(arr[0]);
        Long     msgId     = Long.valueOf(arr[1]);
        this.msgLogService.retry(logId, msgId, FLAG);
    }
}
