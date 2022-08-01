package com.jgdabc.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器
//加注解指定拦截处理异常的Controller
@ResponseBody
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@Slf4j
public class GlobalExceptionHander {

//进行异常处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R_<String> exceptionHandeler(SQLIntegrityConstraintViolationException ex)
    {
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")) {
            String[] spl = ex.getMessage().split(" ");
            String msg = spl[2]+ "已存在";
            return R_.error(msg);
        }
        return  R_.error("未知错误");


    }
    @ExceptionHandler(CustomException.class)
    public R_<String> exceptionHandeler(CustomException ex)
    {
        log.error(ex.getMessage());

        return  R_.error(ex.getMessage());


    }


}
