package com.wgf.consumer;

import com.wgf.base.entity.MsgLog;
import com.wgf.base.mapper.MsgLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MyBatisPlusTest {
    @Autowired
    MsgLogMapper msgLogMapper;

    @Test
    public void selectTest() {
        List<MsgLog> msgLogs = msgLogMapper.selectList(null);
        System.out.println(msgLogs.size());
    }
}
