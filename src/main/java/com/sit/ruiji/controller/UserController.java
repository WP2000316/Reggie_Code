package com.sit.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sit.ruiji.common.R;
import com.sit.ruiji.entity.User;
import com.sit.ruiji.service.UserService;
import com.sit.ruiji.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 模拟接收手机验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){

            //生成随机的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code);

            //通过阿里云向手机发送验证码

            //将生成的验证码保存到Session
            session.setAttribute(phone,code);

            return R.success("短信验证码发送成功");
        }

        return R.error("短信验证码发送失败");
    }

    /**
     * 登录功能
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        String codeInSession =(String) session.getAttribute(phone);

        //进行验证码比较
        if (codeInSession != null && codeInSession.equals(code)){
            //如果对比成功，则表明登录成功
            //判断当前手机号是否为新用户，若为新用户，则自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
