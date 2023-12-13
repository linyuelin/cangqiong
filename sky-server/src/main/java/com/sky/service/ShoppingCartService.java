package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Category;
import com.sky.entity.ShoppingCart;
import com.sky.result.PageResult;
import java.util.List;

public interface ShoppingCartService {

	
	/**
	 * カートに入れる
	 * @param shoppingCartDTO
	 */
	void add(ShoppingCartDTO shoppingCartDTO);

	/**
	 * カート情報を見せる
	 * @return
	 */
	List<ShoppingCart> showShoppingCart();

	/**
	 * カートを空にする
	 */
	void cleanShoppingCart();
    
}
