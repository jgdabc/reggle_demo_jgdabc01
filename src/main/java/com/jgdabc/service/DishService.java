package com.jgdabc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jgdabc.dto.DishDto;
import com.jgdabc.entity.Dish;

public interface DishService extends IService<Dish> {
//新增菜品，同时插入口味数据

    /**
     * 这个是扩展功能，在我们保存菜品数据的时候，在口味表里面也插入改变的数据
     * @param dishDto
     */
    public  void saveWithFlavor(DishDto dishDto);
//    根据id来查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);
//更新菜品信息，同时还要更新响应的口味信息
    public  void updateWithFlavor(DishDto dishDto);
}
