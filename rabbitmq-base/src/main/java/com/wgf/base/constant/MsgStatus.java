package com.wgf.base.constant;

public enum MsgStatus {
    DELIVERY(0, "投递中"),
    SUCCESS(1, "投递成功"),
    FAIL(2, "投递失败"),
    REPAIR(3, "人工修复"),
    RECEIVE_SUCCESS(4, "消费成功"),
    REPEAT(5, "重复消费");
    private Integer code;
    private String  remark;

    MsgStatus(Integer code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public Integer getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }
}
