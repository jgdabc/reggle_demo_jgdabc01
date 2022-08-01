package com.jgdabc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgdabc.common.CustomException;
import com.jgdabc.entity.Category;
import com.jgdabc.entity.Dish;
import com.jgdabc.entity.Setmeal;
import com.jgdabc.mapper.CategoryMapper;
import com.jgdabc.service.CategoryService;
import com.jgdabc.service.DishService;
import com.jgdabc.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    @Override
//    根据id删除分类，删除之前进行判断
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        long count = dishService.count(dishLambdaQueryWrapper);
        if(count>0)
        {
            log.info("检测到关联菜品");
            throw new CustomException("当前分类项关联了菜品，不能删除");
//            说明已经关联了菜品，抛出一个业务异常
        }
//        查询当前分类是否关联菜品，如果关联，就抛出业务异常，
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count1 = setMealService.count(setmealLambdaQueryWrapper);
        if(count1>0)
        {
            log.info("检测到关联套餐");
            throw new CustomException("当前分类下关联了套餐，不能进行删除");

//            说明已经关联了套餐，需要配抛出一个义务异常
        }
        super.removeById(id);


//        查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        //正常删除分类
    }
}
