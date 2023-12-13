package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sky.entity.DishFlavor;


@Mapper
public interface DishFlavorMapper {

	/**
	 * DishFlavorのデータ何個入れる
	 * @param flavors
	 */

	void insertBatch(List<DishFlavor> flavors);

	/**
	 * dishIdによって対応する味を削除する
	 * @param id
	 */
	@Delete("delete from  dish_flavor  where dish_id = #{dishId} ")
	void deleteByDishId(Long dishId);
    
	/**
	 * 対応する味を一括削除する
	 * @param ids
	 */
	void deleteByDishIds(List<Long> dishIds);
    
	
	/**
	 * dishIdによって味をセレクトする
	 * @param Dishid
	 * @return
	 */
	@Select("select * from dish_flavor where dish_id = #{dishId}")
	List<DishFlavor> getByDishId(Long dishId);

	

	
	
	
    
}
