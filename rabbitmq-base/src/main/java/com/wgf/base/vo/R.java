package com.wgf.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 返回VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "R", description = "返回VO")
public class R<T> {

    @ApiModelProperty(value = "返回数据")
    private T       data;
    @ApiModelProperty(value = "响应编码")
    private Integer code;
    @ApiModelProperty(value = "是否成功")
    private Boolean success;
    @ApiModelProperty(value = "响应消息")
    private String  msg;

    public static <T> R<T> success() {
        return success(null, null, null);
    }

    public static <T> R<T> success(T data) {
        return success(data, null, null);
    }

    public static <T> R<T> success(T data, String msg) {
        return success(data, null, msg);
    }

    public static <T> R<T> success(T data, Integer code, String msg) {
        if (Objects.isNull(code)) {
            code = Code.success;
        }
        return new R(data, code, true, msg);
    }

    public static <T> R<T> fail() {
        return fail(null, null, null);
    }

    public static <T> R<T> fail(T data) {
        return fail(data, null, null);
    }

    public static <T> R<T> fail(T data,  String msg) {
        return fail(data, null, msg);
    }

    public static <T> R<T> fail(T data, Integer code, String msg) {
        if (Objects.isNull(code)) {
            code = Code.fail;
        }
        return new R(data, code, false, msg);
    }


    public interface Code {
        int success = 200;
        int fail   = 1000;
    }
}
