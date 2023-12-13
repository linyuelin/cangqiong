package com.sky.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店舗管理")
@Slf4j
public class ShopController {
    
	@Autowired
    private RedisTemplate redisTemplate;
	
    public static final String KEY ="SHOP_STATUS";
	
	/**
	 * 店舗のステータスを調べる
	 * @return
	 */
	@GetMapping("/status")
	@ApiOperation("店舗のステータスを調べる")
	public Result<Integer> getStatus(){
		ValueOperations opsForValue = redisTemplate.opsForValue();
		Integer status = (Integer) opsForValue.get(KEY);
		log.info("今は{}なんです",status == 1? "営業中":"準備中");
		
		return Result.success(status);
	}
}








