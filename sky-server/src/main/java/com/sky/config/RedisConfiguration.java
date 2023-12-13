package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfiguration {
     
	@Bean
	public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		log.info("redisを作り始める");
		RedisTemplate redisTemplate = new RedisTemplate();
		//コネクションファクトリーを設置
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		//シリアライザを設定
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		return redisTemplate;
	}
}
