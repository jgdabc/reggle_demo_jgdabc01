package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgdabc.common.R_;

import com.jgdabc.entity.Employee;
import com.jgdabc.service.EmployService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")

public class EmployController {
    @Autowired
    private EmployService employService;
//RequestBody主要用于接收前端传递给后端的json数据
    @PostMapping("/login")
    public R_<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        ////这里为什么还有接收一个request对象的数据?
        //        //登陆成功后，我们需要从请求中获取员工的id，并且把这个id存到session中，这样我们想要
//        密码加密处理
        String password = employee.getPassword();
//        md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        ////在设计数据库的时候我们对username使用了唯一索引,所以这里可以使用getOne方法
        Employee emp = employService.getOne(queryWrapper);
//        如果没有查到就返回登录失败结果
        if (emp == null) { //如果是空的信息就没有，所以失败，返回全部封装给R_
            return R_.error("登录失败");
        }
//        如果密码比对不成功，也返回登录失败
        if (!emp.getPassword().equals(password)) {
                return R_.error("登录失败");

        }
        if(emp.getStatus()==0) //账号登录也不能登录的就是禁用账号
        {
            return  R_.error("账号已经禁用");

        }
//        登录成功,将id放到session里面，后期有用
         request.getSession().setAttribute("employee",emp.getId());
        //把从数据库中查询到的用户返回出去
        return R_.success(emp);

    }
//    员工退出
    @PostMapping("/logout")
    public  R_<String> logout(HttpServletRequest request)
    {
//        清理Session保存的当前登录员工的id、
        request.getSession().removeAttribute("employee");
        return R_.success("退出成功");


    }
    @PostMapping
    public R_<String>save(HttpServletRequest request ,@RequestBody  Employee employee)
    {
        log.info("新增员工，员工信息:{}",employee.toString());
//        设置初始密码123456，然后进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//      获得登录用户的id
//        long empid = (long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);
        employService.save(employee);
        return  R_.success("新增员工成功");

    }
//   员工信息查询
    @RequestMapping("/page")
    public R_<Page> page(int page,int pageSize,String name)
    {
        log.info("page = {},pagesize ={},name={}",page,pageSize,name);
        Page pageInfo = new Page(page, pageSize);//前端传过来分页的当前码和分页的每一页的大小
        log.info("pageinfo:{}",pageInfo);

        //        构造分页构造器
//        条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
//        添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employService.page(pageInfo,lambdaQueryWrapper);
        return R_.success(pageInfo);
//pageInfo到这里的records 会保存 employee里面的字段数据
    }
//    根据id修改员工信息
    @PutMapping
    public R_<String> update(@RequestBody  Employee employee)
    {
        employService.updateById(employee);


        return R_.success("员工信息修改成功");

    }
    @GetMapping("/{id}")
    public R_<Employee> getById(@PathVariable long id)
    {
        Employee emp = employService.getById(id);
        if(emp!=null)
        {
            return R_.success(emp);
        }
       return R_.error("没有查询到对应数据");

    }
}