package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * カート管理
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
	

	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private SetmealMapper setmealMapper;

	public void add(ShoppingCartDTO shoppingCartDTO) {

		// 既存商品があるかどうか
		ShoppingCart shoppingCart = new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
		Long currentId = BaseContext.getCurrentId();
		shoppingCart.setUserId(currentId);
		List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
		// あるなら、ナンバーをアップデートする
		if (list != null && list.size() > 0) {
			ShoppingCart cart = list.get(0);
			cart.setNumber(cart.getNumber() + 1);
			shoppingCartMapper.updateNumberById(cart);

		} else {
			// なければ、新しく挿入
			Long dishId = shoppingCartDTO.getDishId();
			

			if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                
                
			}else {
				Long setmealId = shoppingCartDTO.getSetmealId();
				Setmeal setmeal = setmealMapper.getById(setmealId);
				shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
			}
			
			shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
		}
	}

	/**
	 * カートに入れてた商品を見せる
	 */
	@Override
	public List<ShoppingCart> showShoppingCart() {
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setUserId(BaseContext.getCurrentId());
		List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
		return list;
	}

	/**
	 * カートを空にする
	 */
	public void cleanShoppingCart() {
		
		shoppingCartMapper.cleanShoppingCart(BaseContext.getCurrentId());
		
	}
	
}







