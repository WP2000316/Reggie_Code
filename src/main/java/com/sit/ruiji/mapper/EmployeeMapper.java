package com.sit.ruiji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sit.ruiji.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
