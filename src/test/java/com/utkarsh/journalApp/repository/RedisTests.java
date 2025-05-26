package com.utkarsh.journalApp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void testRedis(){
//        redisTemplate.opsForValue().set("email", "utkarshdubey0242@gmail.com");
        Object email = redisTemplate.opsForValue().get("email");
        int a = 1;
    }
}
