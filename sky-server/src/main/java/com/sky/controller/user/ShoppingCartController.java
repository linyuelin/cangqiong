package com.sky.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.ShoppingCartDTO;
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

}
