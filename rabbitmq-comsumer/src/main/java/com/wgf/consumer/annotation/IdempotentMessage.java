package com.wgf.consumer.annotation;

import java.lang.annotation.*;

/**
 * 保证消息幂等注解
 * 使用此注解监听器必须要有两个参数
 * Channel, Message
 *
 * 消费失败必须将异常抛出
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentMessage {
}
