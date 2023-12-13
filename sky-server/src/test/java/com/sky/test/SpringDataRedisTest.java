package com.sky.test;



import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import lombok.val;


@SpringBootTest
public class SpringDataRedisTest {

	
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	@Test
	public void testRedisTemplate() {
		System.out.println(redisTemplate);
		ValueOperations opsForValue = redisTemplate.opsForValue();
		HashOperations opsForHash = redisTemplate.opsForHash();
		
	}
	
	

	@Test
	public void testString() {
		
		redisTemplate.opsForValue().set("name", "jack");
		String name= (String) redisTemplate.opsForValue().get("name");
		System.out.println(name);
		
		redisTemplate.opsForValue().set("code", "1234", 10, TimeUnit.SECONDS);
		redisTemplate.opsForValue().setIfAbsent("james", "1");
		redisTemplate.opsForValue().setIfAbsent("james", "2");
		
	}
	
	@Test
	public void testHash() {
		 
		HashOperations opsForHash = redisTemplate.opsForHash();
		
		opsForHash.put("100", "name", "tom");
		opsForHash.put("100", "age", "20");
		
	    String name =(String)opsForHash.get("100", "name"); 
	    System.out.println(name);
	    
	   Set keys =opsForHash.keys("100");
	   System.out.println(keys);
	   
	   List values = opsForHash.values("100");
	   System.out.println(values);
		
	   opsForHash.delete("100", "age");
		
		
	}
	
	
	
}








