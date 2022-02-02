package com.wgf.producer.controller;

import com.alibaba.fastjson.JSON;
import com.wgf.base.entity.MsgLog;
import com.wgf.base.entity.Stock;
import com.wgf.base.vo.R;
import com.wgf.producer.utils.RabbitMqUtils;
import com.wgf.producer.utils.SequenceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试controller")
@RestController
@RequestMapping("/test")
public class MsgController {
    @Value("${mq.stock.exchange}")
    private String exchange;

    @Value("${mq.stock.deduction.routing-key}")
    private String routingKey;

    @Autowired
    RabbitMqUtils rabbitMqUtils;

    @Autowired
    SequenceUtils sequenceUtils;

    @ApiOperation(value = "正常发送测试， 库存扣减1")
    @GetMapping("send_test")
    public R<Void> normalSend() {
        Stock  stock = new Stock().setProductId(1).setStockNum(1);
        String msg   = JSON.toJSONString(stock);
        MsgLog msgLog = new MsgLog()
                .setMsgId(sequenceUtils.getNextId())
                .setExchangeName(exchange)
                .setRoutingKey(routingKey)
                .setMsg(msg);

        rabbitMqUtils.sendMsg(msgLog);
        return R.success();
    }


    @ApiOperation(value = "路由失败重试测试")
    @GetMapping("routing_err")
    public R<Void> routingErr() {
        Stock  stock = new Stock().setProductId(1).setStockNum(1);
        String msg   = JSON.toJSONString(stock);
        MsgLog msgLog = new MsgLog()
                .setMsgId(sequenceUtils.getNextId())
                .setExchangeName(exchange)
                .setRoutingKey("err")
                .setMsg(msg);

        rabbitMqUtils.sendMsg(msgLog);
        return R.success();
    }

    @ApiOperation(value = "消息幂等测试, 发送2条库存扣减1的消息")
    @GetMapping("idempotent")
    public R<Void> idempotent() {
        Stock  stock = new Stock().setProductId(1).setStockNum(1);
        String msg   = JSON.toJSONString(stock);
        Long   msgId = sequenceUtils.getNextId();
        for (int i = 0; i < 2; i++) {
            MsgLog msgLog = new MsgLog()
                    .setMsgId(msgId)
                    .setExchangeName(exchange)
                    .setRoutingKey(routingKey)
                    .setMsg(msg);

            rabbitMqUtils.sendMsg(msgLog);
        }
        return R.success();
    }

    @ApiOperation(value = "消费失败重试测试")
    @GetMapping("fail")
    public R<Void> fail() {
        Stock  stock = new Stock().setProductId(1).setStockNum(10);
        String msg   = JSON.toJSONString(stock);
        MsgLog msgLog = new MsgLog()
                .setMsgId(sequenceUtils.getNextId())
                .setExchangeName(exchange)
                .setRoutingKey(routingKey)
                .setMsg(msg);

        rabbitMqUtils.sendMsg(msgLog);
        return R.success();
    }
}
