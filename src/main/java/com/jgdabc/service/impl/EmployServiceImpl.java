package com.jgdabc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgdabc.entity.Employee;
import com.jgdabc.mapper.EmployMapper;
import com.jgdabc.service.EmployService;
import org.springframework.stereotype.Service;
/*
MyBatis Plus 使用 ServiceImpl 类实现 IService 接口
 */
@Service
public class EmployServiceImpl extends ServiceImpl<EmployMapper, Employee> implements EmployService {

}
