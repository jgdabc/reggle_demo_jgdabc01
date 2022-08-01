package com.jgdabc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.jgdabc.common.BaseContext;
import com.jgdabc.common.R_;
import com.jgdabc.entity.AddressBook;
import com.jgdabc.entity.Setmeal;
import com.jgdabc.service.AddressBookService;
import com.jgdabc.service.SetMealService;
import com.jgdabc.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {


    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public R_<AddressBook> save(@RequestBody AddressBook addressBook, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        addressBook.setUserId(userId);
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R_.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R_<AddressBook> setDefault(@RequestBody AddressBook addressBook,HttpSession session) {
        log.info("addressBook:{}", addressBook);
        Long userId = (Long) session.getAttribute("user");
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R_.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R_ get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R_.success(addressBook);
        } else {
            return R_.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R_<AddressBook> getDefault(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R_.error("没有找到该对象");
        } else {
            return R_.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R_<List<AddressBook>> list(AddressBook addressBook,HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        addressBook.setUserId(userId);
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R_.success(addressBookService.list(queryWrapper));
    }


}
