package com.jgdabc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
//通用返回结果
@Data
public class R_<T>implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据
//这样的泛型设计非常巧妙了
    public static <T> R_<T> success(T object) {
        R_<T> r = new R_<T>();
        r.data = object;//这里可以接收到返回的值
        r.code = 1;
        return r;
    }

    public static <T> R_<T> error(String msg) {
        R_ r = new R_();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R_<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
