package com.jgdabc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jgdabc.entity.Employee;
import org.springframework.stereotype.Service;

/**
 * 除了 BaseMapper 接口，MyBatis Plus 还提供了 IService 接口，该接口对应 Service 层。MyBatis Plus 的通用 Service CRUD 实现了 IService 接口，
 * 进一步封装 CRUD。为了避免与 BaseMapper 中定义的方法混淆，该接口使用 get（查询单行）、remove（删除）、list（查询集合）和 page（分页）前缀命名的方式进行区别。
 */

public interface EmployService extends IService<Employee> {
}
