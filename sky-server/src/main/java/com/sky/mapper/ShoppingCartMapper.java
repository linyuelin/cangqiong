package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.sky.entity.ShoppingCart;

@Mapper
public interface ShoppingCartMapper {
	
	
    /**
     * カートに挿入してた商品をセレクト
     * @param shoppingCart
     */
	List<ShoppingCart>list(ShoppingCart shoppingCart);

	
	/**
	 * 商品ナンバーを変える
	 * @param cart
	 */
	@Update("update Shopping_cart set number = #{number} where id = #{id} ")
	void updateNumberById(ShoppingCart cart);


	/**
	 * カートに入れる
	 * @param shoppingCart
	 */
	@Insert("insert into shopping_cart (name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time) " +
	 "values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
	void insert(ShoppingCart shoppingCart);
   
	

}
