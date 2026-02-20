package com.sit.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sit.ruiji.entity.Orders;

public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
