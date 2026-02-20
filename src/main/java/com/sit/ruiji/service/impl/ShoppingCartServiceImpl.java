package com.sit.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sit.ruiji.entity.ShoppingCart;
import com.sit.ruiji.mapper.ShoppingCartMapper;
import com.sit.ruiji.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
