package com.wgf.producer.mq;

import com.wgf.producer.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 描述：消息可靠性配置 消息投递到交换机ACK确认回调。
 * 如果消息成功投递到交换机，则ack为true
 */
@Slf4j
@Component
public class MsgConfirmCallback implements RabbitTemplate.ConfirmCallback {
    /**
     * 回调标记
     */
    public final static int FLAG = 0;

    @Autowired
    MsgLogService msgLogService;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 如果发送到交换器都没有成功（比如说删除了交换器），ack 返回值为 false
        // 如果发送到交换器成功，但是没有匹配的队列（比如说取消了绑定），ack 返回值为还是 true （这是一个坑，需要注意）
        log.debug("ConfirmCallback , correlationData = {} , ack = {} , cause = {} ", correlationData, ack, cause);

        if (!ack) {
            String   messageId = correlationData.getId();
            String[] arr       = messageId.split("_");
            Long     logId     = Long.valueOf(arr[0]);
            Long     msgId     = Long.valueOf(arr[1]);
            this.msgLogService.retry(logId, msgId, FLAG);
        }
    }
}
