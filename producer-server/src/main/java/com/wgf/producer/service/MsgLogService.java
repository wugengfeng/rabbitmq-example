package com.wgf.producer.service;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgf.base.constant.MsgStatus;
import com.wgf.base.entity.MsgLog;
import com.wgf.base.mapper.MsgLogMapper;
import com.wgf.producer.mq.MsgConfirmCallback;
import com.wgf.producer.mq.MsgReturnCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class MsgLogService extends ServiceImpl<MsgLogMapper, MsgLog> {

    /**
     * 消息重新投递时间间隔：秒
     */
    @Value("${mq.retry.interval}")
    Integer interval;

    /**
     * 重试次数
     */
    @Value("${mq.retry.try-count}")
    Integer tryCount;

    public void saveMsg(MsgLog msgLog) {
        if (Objects.isNull(msgLog.getId())) {
            // 第一次发送消息
            baseMapper.insert(msgLog);
        } else {
            // 消息重新发送，次数+1
            MsgLog update = new MsgLog()
                    .setId(msgLog.getId())
                    .setMsgStatus(MsgStatus.DELIVERY.getCode())
                    .setTryCount(msgLog.getTryCount() + 1);
            baseMapper.updateById(update);
        }
    }

    /**
     * 消息重试
     *
     * @param logId 日志id
     * @param flag  回调标记
     */
    public void retry(Long logId, Long msgId, int flag) {
        MsgLog msgLog = baseMapper.selectById(logId);

        if (Objects.isNull(msgLog)) {
            log.error("消息id：{} 获取不到相应记录", msgId);
            return;
        }

        // 判断是否重试上限
        if (msgLog.getTryCount() >= tryCount) {
            log.error("交换机：{} 路由键：{} 消息id：{} 达到重试上限，转为人工处理", msgLog.getExchangeName(), msgLog.getRoutingKey(), msgId);
            MsgLog update = new MsgLog().setId(logId)
                    .setMsgStatus(MsgStatus.REPAIR.getCode());
            this.updateById(update);
            return;
        }

        // 重新投递间隔时间
        Date nextTryTime = DateUtil.offsetSecond(new Date(), interval);
        MsgLog update = new MsgLog()
                .setId(logId)
                .setMsgStatus(MsgStatus.FAIL.getCode())
                .setNextTryTime(nextTryTime);

        switch (flag) {
            case MsgConfirmCallback.FLAG:
                log.error("交换机：{} 路由键：{} 消息id：{} 投递Broker失败", msgLog.getExchangeName(), msgLog.getRoutingKey(), msgId);
                update.setRemark("投递Broker失败");
                break;
            case MsgReturnCallback.FLAG:
                log.error("交换机：{} 路由键：{} 消息id：{} Exchange路由Queue失败", msgLog.getExchangeName(), msgLog.getRoutingKey(), msgId);
                update.setRemark("Exchange路由Queue失败");
                break;
            default:
                log.error("交换机：{} 路由键：{} 消息id：{} 投递失败", msgLog.getExchangeName(), msgLog.getRoutingKey(), msgId);
                break;
        }

        this.updateById(update);
    }

}
