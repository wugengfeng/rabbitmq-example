package com.wgf.reparation.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.wgf.base.constant.MsgStatus;
import com.wgf.base.entity.MsgLog;
import com.wgf.base.vo.R;
import com.wgf.reparation.api.MsgApi;
import com.wgf.reparation.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 补偿任务
 */
@Slf4j
@Component
public class ReparationTask {

    /**
     * 消息重新发送每批处理条数
     */
    @Value("${task.re-send.batch-size}")
    private Integer batchSize;

    @Autowired
    MsgLogService msgLogService;

    @Autowired
    MsgApi msgApi;

    @Scheduled(cron = "${task.re-send.cron}")
    public void reSend() {
        PageHelper.offsetPage(0, batchSize);
        List<MsgLog> msgLogList = this.msgLogService.list(new QueryWrapper<MsgLog>().lambda()
                .eq(MsgLog::getMsgStatus, MsgStatus.FAIL.getCode())
                .le(MsgLog::getNextTryTime, new Date())
                .orderByAsc(MsgLog::getNextTryTime));

        if (CollectionUtils.isEmpty(msgLogList)) {
            log.info("无须进行补偿发送");
            return;
        }

        for (MsgLog msgLog : msgLogList) {
            try {
                // 调用API重新发送
                msgApi.reSend(msgLog);
            } catch (Exception e) {
                log.error("消息id：{} 重新发送失败", msgLog.getMsgId(), e);
            }
        }
    }
}
