package com.jgdabc.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


//自定义的源数据处理器
@Component
@Slf4j
public class MyMetaObjextHander implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]....");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("公共字段自动填充[insert]");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        long id = Thread.currentThread().getId();
        log.info("当前线程的id为{}",id);
    }
}
