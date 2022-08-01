package com.jgdabc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgdabc.entity.User;
import com.jgdabc.mapper.UserMapper;
import com.jgdabc.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
