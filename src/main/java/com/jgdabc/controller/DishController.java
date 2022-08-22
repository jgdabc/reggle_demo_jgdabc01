package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgdabc.common.R_;
import com.jgdabc.dto.DishDto;
import com.jgdabc.entity.Category;
import com.jgdabc.entity.Dish;
import com.jgdabc.entity.DishFlavor;
import com.jgdabc.service.CategoryService;
import com.jgdabc.service.DishFlavorService;
import com.jgdabc.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 菜品管理，包括口味。只用dish的话，他不会接收前端传过来的口味数据字段，但是我们需要，所以我们需要封装口味数据字段的实体类
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @PostMapping("/status/{status}")
    public R_<String> ban_status(@PathVariable("status") Integer status,long ids)
    {
        Dish dish = dishService.getById(ids);
        if(dish!=null)
        {
            dish.setStatus(status);
            dishService.updateById(dish);
            return  R_.success("状态修改成功");
        }
        return  R_.error("菜品状态修改异常");
    }

    //    新增菜品
    @PostMapping
    public R_<String> save(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R_.success("新增菜品成功");
    }
//
//    @GetMapping("/page")
//    public R_<Page> page(int page, int pageSize, String name) {
////        log.info("page:{}", page);
////        log.info("pageSize:{}", pageSize);
////        log.info("name{}", name);
////       构造一个分页构造器对象
//        Page<Dish> dishPage = new Page<>(page, pageSize);
////        构造一个分页条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
////        根据name去添加条件，作为查询的条件，这个名字啊
////        我们最好去用迷糊查询
//        queryWrapper.like(name!=null,Dish::getName,name);
////        然后做一个排序的条件
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
////        做分页查询具体
//        dishService.page(dishPage,queryWrapper);//最后的数据表其实已经自动处理封装到dishPage里面
//
////        返回给前端以R_对象封装的数据对象
//        return R_.success(dishPage);
//


//        return null;

//        //构造一个分页构造器对象
//        Page<Dish> dishPage = new Page<>(page,pageSize);
//
//        //构造一个条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加过滤条件 注意判断是否为空  使用对name的模糊查询
//        queryWrapper.like(name != null,Dish::getName,name);
//        //添加排序条件  根据更新时间降序排
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
//        //去数据库处理分页 和 查询
//        dishService.page(dishPage,queryWrapper);
//
//        //因为上面处理的数据没有分类的id,这样直接返回R.success(dishPage)虽然不会报错，但是前端展示的时候这个菜品分类这一数据就为空
//        return R_.success(dishPage);
//    }

    @GetMapping("/page")
    public R_<Page<DishDto>> page(int page, int pageSize, String name) {

        //构造一个分页构造器对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //上面对dish泛型的数据已经赋值了，这里对DishDto我们可以把之前的数据拷贝过来进行赋值

        //构造一个条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件 注意判断是否为空  使用对name的模糊查询
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件  根据更新时间降序排
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //去数据库处理分页 和 查询
        dishService.page(dishPage, queryWrapper);//这样是原始的未扩展的分页查询的数据的封装。

        //获取到dish的所有数据 records属性是分页插件中表示分页中所有的数据的一个集合
        List<Dish> records = dishPage.getRecords();
//        item代表遍历出来的每一个菜品就是Dish
//        这里类似一个遍历属性赋值的过程。
        List<DishDto> list = records.stream().map((item) -> {
            //对实体类DishDto进行categoryName的设值

            DishDto dishDto = new DishDto();
            //这里的item相当于Dish  对dishDto进行除categoryName属性的拷贝
            BeanUtils.copyProperties(item, dishDto);
            //获取分类的id
            Long categoryId = item.getCategoryId();
            //通过分类id获取分类对象

            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //设置实体类DishDto的categoryName属性值。这里是调用到category的查询查询到name然后赋值给具体的dto中扩展的属性
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;//返回每一个dto
        }).collect(Collectors.toList());//这里收集起来给到列表

        //对象拷贝  使用框架自带的工具类，第三个参数是不拷贝到属性
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");//除去records以外的属性的拷贝
        dishDtoPage.setRecords(list);//将收集起来的list赋值给最终的dto的records
        //因为上面处理的数据没有分类的id,这样直接返回R.success(dishPage)虽然不会报错，但是前端展示的时候这个菜品分类这一数据就为空
        //所以进行了上面的一系列操作
        return R_.success(dishDtoPage);//最终返回。
    }

    /**
     * 根据id来查询菜品信
     * 息和对应的口味信息
     *
     * @param
     * @return
     */
    @GetMapping("/{id}")
    public R_<DishDto> get(@PathVariable Long id)
    {

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return  R_.success(dishDto);

    }
//    修改菜品
    @PutMapping
    public R_<String> update(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R_.success("修改菜品成功");
    }
//    根据条件查询对应的菜品数据
    @GetMapping("/list")
    public R_<List<DishDto>> list(Dish dish)
    {
//        构造查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//       添加条件为在售的展示出来
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        //        添加一个排序条件
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
         List<DishDto> disdtoList =  list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
//            根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(categoryId!=null)
            {
                String catagoryName = category.getName();
                dishDto.setCategoryName(catagoryName);
            }
//            当前菜品的id
            Long dishid = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishid);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return  dishDto;
        }).collect(Collectors.toList());

        return R_.success(disdtoList);
    }

}