package com.wgf.consumer.aop;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import com.wgf.base.constant.MsgStatus;
import com.wgf.base.entity.MsgLog;
import com.wgf.base.mapper.MsgLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * AOP切面扩展 @RabbitListener 注解功能
 * 保证消息幂等
 */
@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class RabbitListenerAop {

    /**
     * 消费异常允许重新消费次数
     */
    @Value("${mq.receive.receive-count}")
    public Integer receiveCount;

    /**
     * key前缀
     */
    @Value("${redis.key-prefix}")
    public String keyPrefix;

    /**
     * 保证幂等时间：单位秒
     */
    @Value("${redis.idempotent-time}")
    public Integer idempotentTime;

    @Autowired
    MsgLogMapper msgLogMapper;

    @Autowired
    RedisTemplate redisTemplate;


    @Pointcut("@annotation(com.wgf.consumer.annotation.IdempotentMessage)")
    public void pointCut() {
    }


    @Around("pointCut()")
    public void around(ProceedingJoinPoint proceedingJoinPoint) {

        // 如果获取不到消息实体，为了数据不丢失，必须让其消费
        Message message = this.getParam(proceedingJoinPoint, Message.class);
        Long    logId   = null;
        MsgLog  msgLog  = null;
        String  key     = null;

        try {
            if (Objects.nonNull(message)) {
                // 保证消息幂等
                String   messageId = message.getMessageProperties().getMessageId();
                String[] arr       = messageId.split("_");
                logId = Long.valueOf(arr[0]);
                Long msgId = Long.valueOf(arr[1]);

                key = String.format("%s%s", keyPrefix, msgId);
                Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "", idempotentTime, TimeUnit.SECONDS);

                // 如果key存在则说明正在消费或者被消费过了
                if (!success) {
                    this.rejectMsg(proceedingJoinPoint, logId, msgId, message, 1);
                    return;
                }

                // 重新消费次数达到上限
                msgLog = this.msgLogMapper.selectOne(new QueryWrapper<MsgLog>().lambda()
                        .eq(MsgLog::getId, logId)
                        .select(MsgLog::getReceiveCount));
                if (msgLog.getReceiveCount() >= receiveCount) {
                    this.rejectMsg(proceedingJoinPoint, logId, msgId, message, 2);
                }
                return;
            }

            // 消息消费，返回true否则返回false
            proceedingJoinPoint.proceed();

            if (Objects.nonNull(logId) && Objects.nonNull(msgLog)) {
                MsgLog update = new MsgLog().setId(logId).setReceiveCount(msgLog.getReceiveCount() + 1);
                // 消费成功
                update.setMsgStatus(MsgStatus.RECEIVE_SUCCESS.getCode());
                this.msgLogMapper.updateById(update);
            }

        } catch (Throwable throwable) {
            // 消费失败，这里不处理，监听器手动Nack
            if (StrUtil.isNotEmpty(key)) {
                this.redisTemplate.delete(key);
            }

            // 消费次数+1
            if (Objects.nonNull(msgLog)) {
                MsgLog update = new MsgLog()
                        .setId(logId)
                        .setReceiveCount(msgLog.getReceiveCount() + 1);
                this.msgLogMapper.updateById(update);
            }
            log.error("消息消费失败", throwable);
        }
    }


    /**
     * 拒绝消息转为人工处理或标记为重复消息
     *
     * @param proceedingJoinPoint
     * @param logId
     * @param flag                1: 消息幂等，重复消费， 2：消费失败，消息重回队列次数上限
     */
    private void rejectMsg(ProceedingJoinPoint proceedingJoinPoint, Long logId, Long msgId, Message message, int flag) {
        MsgLog update = new MsgLog()
                .setId(logId);

        Channel channel = this.getParam(proceedingJoinPoint, Channel.class);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("拒绝消息异常", e);
        }

        if (flag == 1) {
            // 重复消费，保证消息幂等
            update.setMsgStatus(MsgStatus.REPEAT.getCode());
            update.setRemark(String.format("消息重复 id=%s 的消息已消费过一次", msgId));
            log.error("消息id：{} 重复消费已被拦截丢弃", msgId);
        } else {
            // 重新消费上限
            update.setMsgStatus(MsgStatus.REPAIR.getCode());
            update.setRemark("消息重新消费次数已达上限");
            log.error("消息id：{} 重新消费已达上限，无法重回队列", msgId);
        }
        this.msgLogMapper.updateById(update);
    }


    /**
     * 获取参数
     *
     * @param proceedingJoinPoint
     * @return
     */
    private <T> T getParam(ProceedingJoinPoint proceedingJoinPoint, Class<T> clazz) {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args == null || args.length == 0) {
            log.error("消息监听器缺少 {} 类型参数，请检查 @RabbitListener 注解的消息监听器", clazz.getName());
            return null;
        }

        T t = (T) Arrays.stream(args)
                .filter(clazz::isInstance)
                .findAny()
                .orElse(null);

        if (Objects.isNull(t)) {
            log.error("消息监听器缺少 {} 类型参数，请检查 @RabbitListener 注解的消息监听器", clazz.getName());
        }

        return t;
    }
}
