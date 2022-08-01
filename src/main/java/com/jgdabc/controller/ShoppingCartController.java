package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jgdabc.common.BaseContext;
import com.jgdabc.common.R_;
import com.jgdabc.entity.ShoppingCart;
import com.jgdabc.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
//    添加购物车
    @PostMapping("/add")
    public R_<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session)
    {
        log.info("购物车数据{}",shoppingCart);
        //这只用户id指定是哪个用户的购物车数据
//        查询当前用户的菜品或者套餐是否在购物车上
//        如果已经存在就在原来的数量的基础上进行加一
//        如果不存在，则添加到购物车，数量默认是一
//        获得当前用户的id
       Long userId = (Long) session.getAttribute("user");

//        将这个id设置到购物车当中去
        shoppingCart.setUserId(userId);
//        查询当前菜品或者套餐是否在购物车当中、
//        还需要判断当前添加的是套餐还是菜品
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加用户条件
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        if(dishId!=null)
        {
//            添加的是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
//            添加的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
//        查询当前菜品或者套餐是否在购物车当中，如果能够查出来，说明已经存在
        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
       if(one!=null)
       {
//           如果已经存在，那么就在原先的数量上加一
           Integer number = one.getNumber();
           one.setNumber(number+1);
           shoppingCartService.updateById(one);
       }else {
           shoppingCart.setNumber(1);
           shoppingCart.setCreateTime(LocalDateTime.now());
           shoppingCartService.save(shoppingCart);
           one = shoppingCart;

       }
      return R_.success(one);
    }
//    查看购物车
    @GetMapping("/list")
    public R_<List<ShoppingCart>> list(HttpSession session)
    {
        log.info("查看购物车:{}");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,(Long)session.getAttribute("user"));

        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);


       return R_.success(list);
    }
//    清空购物车数据
    @DeleteMapping("/clean")
    public R_<String>clean()
    {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return  R_.success("清空购物车成功");

    }



}
