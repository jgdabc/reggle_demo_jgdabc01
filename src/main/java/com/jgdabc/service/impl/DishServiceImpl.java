package com.jgdabc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgdabc.dto.DishDto;
import com.jgdabc.entity.Dish;
import com.jgdabc.entity.DishFlavor;
import com.jgdabc.mapper.DishMapper;
import com.jgdabc.service.DishFlavorService;
import com.jgdabc.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    /**
     * 新增菜品，同时保存口味数据
     */
    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
//        保存菜品的基本信息

        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();//获取到对应id的菜品口味
       flavors = flavors.stream().map((item)->
        {
            //拿到的这个item就是这个DishFlavor集合
            item.setDishId(dishId);//让口味和id 上
            return item;
        }).collect(Collectors.toList());
//        保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);//批量保存


    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        先查询菜品的基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);
//        查询菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return  dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
//        更新dish表
        this.updateById(dishDto);
//        更新口味表dish_flavor
        /**
         * 可以有这样一个操作逻辑
         * 就是先清理掉口味表数据，然后在重新插入更新的数据
         */
//        清理掉口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

//添加到新的数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->
        {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }
}
