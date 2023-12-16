package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
	
    
	/**
	 * オーダーをコミットする
	 * @param ordersSubmitDTO
	 * @return 
	 */
	OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
   
	
}
