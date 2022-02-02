package com.wgf.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wgf.base.entity.Stock;
import org.apache.ibatis.annotations.Update;

public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 库存扣减
     * @param stock
     */
    @Update("update stock set stock_num = stock_num - #{stockNum} where product_id = #{productId}")
    void deduction(Stock stock);
}
