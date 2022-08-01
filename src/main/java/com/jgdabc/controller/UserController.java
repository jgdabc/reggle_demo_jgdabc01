package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jgdabc.common.BaseContext;
import com.jgdabc.common.R_;
import com.jgdabc.entity.User;
import com.jgdabc.service.UserService;
import com.jgdabc.utils.SMSUtils;
import com.jgdabc.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    private R_<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();//生成四位的验证码
            log.info("code={}", code);
//            SMSUtils.sendMessage("","","","");//这里可以自己调用阿里云的接口
//            生成验证码，也可以自己模拟一下，用工具类将验证码生成一下，只要知道这个验证码就可以
//            然后手机端就可以进行登录
//将生成的验证码保存到Session
            session.setAttribute(phone, code);
            return R_.success("手机验证码短信发送成功");

        }

//        获取手机号
//        生成随机验证码
//        调用阿里云的发送短信
//        要将生成的验证码保存一下，然后比较
//        可以将验证码保存在session当中

        return R_.error("短信发送失败");

    }


    //    移动应用登录端
    @PostMapping("/login")
//    这里使用map来接收前端传过来的值
    private R_<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
//        使用map来接收参数,接收键值参数、
//        编写处理逻辑
//        获取到手机号
//        获取到验证码
//        从Session中获取到保存的验证码
//     将session中获取到的验证码和前端提交过来的验证码进行比较，这样就可以实现一个验证的方式
//        比对页面提交的验证码和session中
//判断当前的手机号在数据库查询是否有记录，如果没有记录，说明是一个新的用户，然后自动将这个手机号进行注册
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInSession = session.getAttribute(phone);
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            return R_.success(user);



            }


        return R_.error("验证失败");
    }
}

