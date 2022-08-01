package com.jgdabc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgdabc.entity.DishFlavor;
import com.jgdabc.mapper.DishFlavorMapper;
import com.jgdabc.service.DishFlavorService;
import com.jgdabc.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
