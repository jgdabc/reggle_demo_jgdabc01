package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgdabc.common.R_;
import com.jgdabc.entity.Orders;
import com.jgdabc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    //用户下单
    @PostMapping("/submit")
    public R_<String> submit(@RequestBody Orders orders, HttpSession session)
    {
        log.info("订单数据{}",orders);
        orderService.submit(orders);
        return  R_.success("下单成功");
    }
    @GetMapping("/page")
    public R_<Page>page(int page,int pageSize,String number,String beginTime,String endTime)
    {
       //分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
//        构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number!=null,Orders::getNumber,number).gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        orderService.page(pageInfo,queryWrapper);
        return R_.success(pageInfo);



//        return null;


    }

}
