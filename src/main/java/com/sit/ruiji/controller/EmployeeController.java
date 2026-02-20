package com.sit.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sit.ruiji.common.R;
import com.sit.ruiji.entity.Employee;
import com.sit.ruiji.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        /**
         * 1.将页面提交的密码进行MD5加密
         * 2.根据页面提交的username进行数据库查询
         * 3.如果没有查询到，返回登录失败
         * 4.密码对比，返回登录结果
         * 5.查看员工状态，如果为禁用状态，返回禁用结果
         * 6.登陆成功，将员工id存入session并返回登录成功结果
         */

        //1.将页面提交的密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的username进行数据库查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到，返回登录失败
        if (emp == null){
            return R.error("登录失败,用户不存在");
        }

        //4.密码对比，返回登录结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败,密码错误");
        }

        //5.查看员工状态，如果为禁用状态，返回禁用结果
        if (emp.getStatus() == 0){
            return R.error("登录失败,账号禁用");
        }

        //6.登陆成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        /**
         * 1.清除Session中的用户id
         * 2.返回结果
         */
        //清除Session中的用户id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息，{}",employee.toString());

        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户ID
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        //设置创建和更新用户信息
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //保存用户信息到数据库
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工分页查询功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("查询信息：{}，{}，{}",page,pageSize,name);

        //构造分页器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 员工状态信息更新功能
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工修改信息成功");
    }

    /**
     * 根据员工id查询信息,用于编辑员工信息功能
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("编辑员工信息功能");
        Employee emp = employeeService.getById(id);
        if (emp != null){
            return R.success(emp);
        }
        return R.error("没用查询到员工信息");
    }

}
