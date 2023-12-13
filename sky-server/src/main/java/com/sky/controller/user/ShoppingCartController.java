package com.sky.controller.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "ショッピングカート")
@Slf4j
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	@PostMapping("/add")
	@ApiOperation("カートに入れる")
	public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
		log.info("ShoppingCartDTO = {}", shoppingCartDTO);
		shoppingCartService.add(shoppingCartDTO);
		return Result.success();
	}
	
	@GetMapping("/list")
	@ApiOperation("カート情報を見せる")
	public Result<List<ShoppingCart>> list(){
		List<ShoppingCart> list = shoppingCartService.showShoppingCart();
		return Result.success(list);
	}
	
	@DeleteMapping("/clean")
	@ApiOperation("カートを空にする")
	public Result clean() {
		shoppingCartService.cleanShoppingCart();
		return Result.success();
	}

}
