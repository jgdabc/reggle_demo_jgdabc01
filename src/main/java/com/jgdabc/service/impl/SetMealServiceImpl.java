package com.jgdabc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.jgdabc.common.CustomException;
import com.jgdabc.dto.SetmealDto;
import com.jgdabc.entity.Setmeal;
import com.jgdabc.entity.SetmealDish;
import com.jgdabc.mapper.SetMealMapper;
import com.jgdabc.service.SetMealService;
import com.jgdabc.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService{
//   新增套餐，同时保存套餐和菜品的关联关系
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional //加入事务保证数据的一致性
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->
        {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
//        保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);


    }
//    删除套餐同时删除套餐和菜品的关联数据
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        查询套餐的状态确定是否可以删除
//        要根据套餐的状态，是在售卖还是停止售卖
//        如果了可以删除，删除套餐中的数据，然后删除关联表当中的数据
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        long count = this.count(setmealLambdaQueryWrapper);
        if(count>0)
        {
            throw  new CustomException("套餐正在售卖，不能删除");
        }
        this.removeBatchByIds(ids);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public void updateSetmealStatusById(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(ids !=null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);
        for(Setmeal setmeal: list)
        {
            if(setmeal !=null)
            {
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }

        }

    }

    @Override
    public SetmealDto getDate(Long id) {
        Setmeal setmeal =this.getById(id);//根据id查询到套餐
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);//这里根据套餐id查询关联的菜品
        if (setmeal!=null)//查询到的套餐不是空
        {
//            拷贝一下数据
            BeanUtils.copyProperties(setmeal,setmealDto);//先将套餐的的数据字段拷贝到扩展的实体类
            List<SetmealDish> list = setmealDishService.list(queryWrapper);//这是查询到的菜品数据
            setmealDto.setSetmealDishes(list);//将菜品数据传过去
            return setmealDto;
        }

        return null;
    }



}
