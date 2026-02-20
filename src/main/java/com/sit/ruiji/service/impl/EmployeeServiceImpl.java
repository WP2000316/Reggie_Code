package com.sit.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sit.ruiji.entity.Employee;
import com.sit.ruiji.mapper.EmployeeMapper;
import com.sit.ruiji.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
