package com.wgf.reparation;

import com.github.pagehelper.PageHelper;
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

    @Test
    public void pageTest() {
        PageHelper.offsetPage(10, 5);
        List<MsgLog> msgLogs = msgLogMapper.selectList(null);
        System.out.println(msgLogs.size());
    }
}
