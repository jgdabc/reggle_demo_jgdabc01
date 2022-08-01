package com.jgdabc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jgdabc.dto.SetmealDto;
import com.jgdabc.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
//    新增套餐，同时保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);
// 删除套餐的时候将关联的菜品都删除掉
    public void removeWithDish(List<Long> ids);
    void updateSetmealStatusById(Integer status,List<Long>id );
    SetmealDto getDate(Long id);



}
