package com.jgdabc.reggle_demo_jgdabc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTestDemo {
//    @Autowired
//    RedisTemplate redisTemplate; //把这个换掉，因为还需要配置类
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public void TestString()
    {
        /**
         * 操作简单的String类型的数据
         */

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("city","beijing");
        String city = (String) valueOperations.get("city");
        System.out.println(city);
        String  city1 = String.valueOf(valueOperations.setIfAbsent("city", "1234"));
        System.out.println(city1);
        valueOperations.set("兰舟千帆", "jgdabc", 10);//这里指定了数据存储出的有效时间

    }
//    操作Hash类型的数据
    public void testHash()
    {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put("002","name",";li");
        hash.put("002","age","20");
        String  name = (String) hash.get("002", "name");
        System.out.println(name);
        Set<Object> keys = hash.keys("002");//获得key
        for (Object key:keys)
        {
            System.out.println(key);
        }
        List<Object> values = hash.values("002");
        for(Object value:values)
        {
            System.out.println(value);
        }


    }
//    操作list类型的数据
    public void testList()
    {
        ListOperations<String, String> list = redisTemplate.opsForList();
        list.leftPush("mylist","list");
        list.leftPushAll("jgdabc","兰舟千帆","光","dao");
        List<String> jgdabc = list.range("jgdabc", 0, -1);
        for(Object jgdabc01:jgdabc)
        {
            System.out.println(jgdabc01);
        }
        String mylist = list.rightPop("mylist");
        Long size = list.size("jgdabc");
        int i = size.intValue();
        for (int i1 = 0; i1 < i; i1++) {
//            出队列
            String jgdabc1 = list.rightPop("jgdabc");
            System.out.println(jgdabc1);

        }


    }

    /**
     * 操作set类型的数据
     */
    @Test
    public void testSet()
    {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add("myset","a","b","c","a");
        Set<String> myset = setOperations.members("myset");
        for (String s : myset) {
            System.out.println(s);
        }
        Long myset1 = setOperations.remove("myset", "a,b");
        System.out.println(myset1);
//        再来获取值
        Set<String> myset2 = setOperations.members("myset");
        for (String s : myset2) {
            System.out.println(s);
        }

    }
    @Test
    public void testZset()
    {
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
        //存值
        zSet.add("myZset","a",10);
        zSet.add("myZset","b",11);
        zSet.add("myZset","c",9);

//        取值
        Set<String> myzset = zSet.range("myzset", 0, -1);
        for (String s : myzset) {
            System.out.println(s);
        }
//        修改元素的分数
        zSet.incrementScore("myzset","c",9);
//        删除
        zSet.remove("myzset","a","b");
        /**
         * 通用操作,针对不同的类型都可以操作
         */
//        通用操作,针对不同的数据类型都可以操作



        }
    @Test
    public void testCommon()
    {
//            判断Redis中所有的key
//        获得所有的key
        Set<String> keys = redisTemplate.keys("*");
        for(String key: keys)
        {
            System.out.println(key);
        }
//        判断某个key是否存在
        redisTemplate.hasKey("myzset");
//        刷出指定的key
        redisTemplate.delete("myzset");
//        获取指定key的数据类型
        DataType myzset = redisTemplate.type("myzset");


//
    }


}
