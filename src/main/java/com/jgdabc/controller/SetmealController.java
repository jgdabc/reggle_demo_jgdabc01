package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgdabc.common.R_;
import com.jgdabc.dto.SetmealDto;
import com.jgdabc.entity.Category;
import com.jgdabc.entity.Dish;
import com.jgdabc.entity.Setmeal;
import com.jgdabc.entity.SetmealDish;
import com.jgdabc.service.CategoryService;
import com.jgdabc.service.DishService;
import com.jgdabc.service.SetMealService;
import com.jgdabc.service.SetmealDishService;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//套餐管理
@RestController
@RequestMapping("/setmeal")

public class SetmealController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    DishService dishService;
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @PostMapping
    public R_<String> save(@RequestBody SetmealDto setmealDto)
    {
        log.info("套餐信息:{}",setmealDto);
       setMealService.saveWithDish(setmealDto);
       return  R_.success("新增套餐成功");
    }
//    套餐分页查询
    @GetMapping("/page")
    public R_<Page> page(int page,int pageSize,String name)
    {

//        构造一个1分页1构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> DtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        根据添加name是否查询，如果name为空就不会作为查询条件1，如果不为空就作为查询条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
//        添加一个排序条件，根据更新时间来进行降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(pageInfo,queryWrapper);
//        拷贝属性
        BeanUtils.copyProperties(pageInfo,DtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
       List<SetmealDto> list = records.stream().map((item)->
        {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();
            //根据分类的id来查询分类的对象
            Category category = categoryService.getById(categoryId);
            if(category!=null)
            {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);

            }
            return  setmealDto;
        }).collect(Collectors.toList());
        DtoPage.setRecords(list);
        return  R_.success(DtoPage);


    }
    @DeleteMapping
    public R_<String> delete(@RequestParam List<Long> ids)
    {
        log.info("ids:{}",ids);
        setMealService.removeWithDish(ids);
        return  R_.success("套餐数据删除成功");

    }
    @GetMapping("list")
    public R_<List<Setmeal>>list( Setmeal setmeal)
    {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setMealService.list(setmealLambdaQueryWrapper);
        return R_.success(list);
    }
//    自己实现的功能
//    对菜品停售卖或者起卖
    @PostMapping("/status/{status}")
    public R_<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        log.info("status:{}",status);
        log.info("ids:{}",ids);
        setMealService.updateSetmealStatusById(status,ids);
        return R_.success("操作成功");



    }
    /*这里主要做一个数据回显*/
    @GetMapping("/{id}")
    public R_<SetmealDto> getData(@PathVariable Long id)
    {
        SetmealDto setmealDto = setMealService.getDate(id);
        return  R_.success(setmealDto);
    }
    @PutMapping
    public R_<String> edit(@RequestBody SetmealDto setmealDto)
    {
        if(setmealDto==null)
        {
            return  R_.error("请求异常");
        }
        if(setmealDto.getSetmealDishes()==null)
        {
            return R_.error("套餐没有菜品，请添加");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);
        //为setmeal_dish表填充相关的属性
        for(SetmealDish setmealDish:setmealDishes)
        {
            setmealDish.setSetmealId(setmealId);//填充属性值
        }
        //批量把setmealDish保存到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);//保存套餐关联菜品
        setMealService.updateById(setmealDto);//保存套餐
        return R_.success("套餐修改成功");

    }
}
