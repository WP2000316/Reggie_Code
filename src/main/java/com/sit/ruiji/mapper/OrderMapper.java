package com.sit.ruiji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sit.ruiji.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
