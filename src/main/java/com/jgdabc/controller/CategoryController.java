package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgdabc.common.R_;
import com.jgdabc.entity.Category;
import com.jgdabc.service.CategoryService;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//分类

@Slf4j
@RestController
@RequestMapping("/category")

public class CategoryController {
    @Autowired
    /**
     * 新增分类
     */

    private CategoryService categoryService;
    @PostMapping
    public R_<String> save (@RequestBody Category category)
    {
        log.info("category:{}",category);
        categoryService.save(category);
        return  R_.success("新增分类成功");
    }
    @GetMapping("/page")
    public R_<Page> page(int page,int pageSize)
    {
//        分页构造器
        Page<Category> pageinfo = new Page<>(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//添加排序条件，根据sort
        queryWrapper.orderByAsc(Category::getSort);
//        进行分页查询
        categoryService.page(pageinfo,queryWrapper);
        return R_.success(pageinfo);


    }
//    根据id来删除分类
    @DeleteMapping
    public R_<String>delete(Long id)
    {
        log.info("删除分类：id为：{}",id);
//        categoryService.removeById(id);
        categoryService.remove(id);
        return R_.success("分类信息删除成功");

    }
//    根据id修改分类信息
    @PutMapping
    public R_<String> update(@RequestBody Category category)
    {
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R_.success("修改分类信息成功");
    }

    /**
     * 根据条件来查询分类数据
     * @param category
     * @return
     */
//    前端传过来一个type类型的参数，这里采用category接收是因为Category包含了这个属性，
//    返回的数据多了，你自己用啥取啥就行
    @GetMapping("list")
    public R_<List<Category>> list(Category category)
    {
//        条件构造器
        LambdaQueryWrapper<Category> QueryWrapper = new LambdaQueryWrapper<>();
//        添加条件
        QueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
//添加排序条件
        QueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(QueryWrapper);

       return  R_.success(list);
    }

}
