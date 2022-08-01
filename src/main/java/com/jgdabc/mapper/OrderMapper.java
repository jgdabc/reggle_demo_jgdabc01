package com.jgdabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jgdabc.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
