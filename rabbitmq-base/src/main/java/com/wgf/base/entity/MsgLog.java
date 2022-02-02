package com.wgf.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author wgf
 * @description 消息日志表
 */
@Data
@Accessors(chain = true)
public class MsgLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息唯一id
     */
    private Long   msgId;
    /**
     * 交换机
     */
    private String exchangeName;

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 投递状态0：投递中1：投递成功2：投递失败3：人工修复4：消费成功5：重复消费
     */
    private Integer msgStatus;

    /**
     * 消息投递重试次数
     */
    private Integer tryCount;

    /**
     * 重新消费消息次数
     */
    private Integer receiveCount;

    /**
     * 下次重试时间
     */
    private Date nextTryTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;
}