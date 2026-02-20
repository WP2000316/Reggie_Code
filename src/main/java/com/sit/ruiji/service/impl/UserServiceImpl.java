package com.sit.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sit.ruiji.entity.User;
import com.sit.ruiji.mapper.UserMapper;
import com.sit.ruiji.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
