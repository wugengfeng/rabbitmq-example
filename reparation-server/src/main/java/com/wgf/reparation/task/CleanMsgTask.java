package com.wgf.reparation.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wgf.base.constant.MsgStatus;
import com.wgf.base.entity.MsgLog;
import com.wgf.reparation.service.MsgLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 异步清理消息日志任务
 */
@Component
public class CleanMsgTask {

    /**
     * 消费成功消息清理延迟时间
     */
    @Value("${task.success.delay}")
    private Integer successDelay;

    /**
     * 重复消费消息清理延迟时间
     */
    @Value("${task.repeat.delay}")
    private Integer repeatDelay;

    @Autowired
    MsgLogService msgLogService;


    /**
     * 清理堆积的消费成功消息
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void cleanSuccessMsg() {
        Date cleanDate = DateUtil.offsetMinute(new Date(), -successDelay);
        msgLogService.remove(new QueryWrapper<MsgLog>().lambda()
        .eq(MsgLog::getMsgStatus, MsgStatus.SUCCESS.getCode())
        .le(MsgLog::getNextTryTime, cleanDate));
    }


    /**
     * 清理堆积的重复消费消息
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void cleanRepeatMsg() {
        Date cleanDate = DateUtil.offsetMinute(new Date(), -repeatDelay);
        msgLogService.remove(new QueryWrapper<MsgLog>().lambda()
                .eq(MsgLog::getMsgStatus, MsgStatus.REPEAT.getCode())
                .le(MsgLog::getNextTryTime, cleanDate));
    }
}
