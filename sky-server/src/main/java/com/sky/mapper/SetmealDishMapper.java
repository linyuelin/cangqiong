package com.sky.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import com.sky.entity.SetmealDish;

@Mapper
public interface SetmealDishMapper {
   
	
	
	/**
     * idによって関連の定食をセレクトする
     *
     * @param dishIds
     * @return
     */
	 
	
   List<Long>  getSetmealDishIdsByDishIds(List<Long> dishIds);
   
   
   
   /**
    * 批量保存套餐和菜品的关联关系
    *
    * @param setmealDishes
    */
   void insertBatch(List<SetmealDish> setmealDishes);

   /**
    * 根据套餐id删除套餐和菜品的关联关系
    *
    * @param setmealId
    */
   @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
   void deleteBySetmealId(Long setmealId);
}
