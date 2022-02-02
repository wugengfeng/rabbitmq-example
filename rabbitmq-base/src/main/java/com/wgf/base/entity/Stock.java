package com.wgf.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 库存
 */
@Data
@Accessors(chain = true)
public class Stock {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 库存数量
     */
    private Integer stockNum;
}
