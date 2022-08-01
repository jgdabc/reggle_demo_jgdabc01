package com.jgdabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jgdabc.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

// Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能
@Mapper
public interface EmployMapper extends BaseMapper<Employee> {
}
