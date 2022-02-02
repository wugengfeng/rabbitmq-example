package com.wgf.producer.controller;

import com.wgf.base.entity.MsgLog;
import com.wgf.base.vo.R;
import com.wgf.producer.utils.RabbitMqUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息重新发送API
 */
@Slf4j
@Api(tags = "API")
@RestController
@RequestMapping("api/v1/")
public class ApiController {

    @Autowired
    RabbitMqUtils rabbitMqUtils;

    @ApiOperation(value = "发送消息API")
    @PostMapping("send")
    public R<Void> send(@RequestBody MsgLog msgLog) {
        try {
            rabbitMqUtils.sendMsg(msgLog);
            return R.success();
        } catch (Exception e) {
            log.error("消息重新发送失败", e);
            return R.fail();
        }
    }
}
