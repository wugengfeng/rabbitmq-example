package com.wgf.reparation.api;

import com.wgf.base.entity.MsgLog;
import com.wgf.base.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msgApi", url = "${api.msg}")
public interface MsgApi {

    /**
     * 消息重新发送API
     * @param msgLog
     * @return
     */
    @PostMapping("api/v1/send")
    R<Void> reSend(@RequestBody MsgLog msgLog);
}
