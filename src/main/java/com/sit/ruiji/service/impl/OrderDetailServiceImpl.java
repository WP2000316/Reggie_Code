package com.sit.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sit.ruiji.entity.OrderDetail;
import com.sit.ruiji.mapper.OrderDetailMapper;
import com.sit.ruiji.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
