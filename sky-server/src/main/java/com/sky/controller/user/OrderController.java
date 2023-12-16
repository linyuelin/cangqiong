package com.sky.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/order")
@Api(tags = "オーダー管理")
@Slf4j
public class OrderController {
	
	@Autowired
	private OrderService orderService;
     
	/**
	 * オーダーをコミットする
	 * @param ordersSubmitDTO
	 * @return
	 */
	@PostMapping("/submit")
	@ApiOperation("オーダー提出する")
	public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
		log.info("オーダーコミットのなかで受け取ったデータは　{}",ordersSubmitDTO);
		OrderSubmitVO  orderSubmitVO =orderService.submitOrder(ordersSubmitDTO);
		return Result.success(orderSubmitVO);
	}
}


















