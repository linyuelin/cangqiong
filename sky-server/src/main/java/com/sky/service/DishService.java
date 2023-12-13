package com.sky.service;

import java.util.List;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService {
	

	/**
	 * 料理の種類増加
	 * @param dishDTO
	 * @return
	 */
	void saveWithFlavor(DishDTO dishDTO);

	
	/**
	 * メニューのページング検索
	 * @param dishPageQueryDTO
	 * @return
	 */
	PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


	/**
	 * メニューの削除
	 * @param ids 
	 * @return
	 */
	void deleteById(List<Long> ids);

	
	/**
	 * idによってセレクト
	 * @param id 
	 * @return
	 */
	DishVO getById(Long id);

	/**
	 * 料理の情報をアップデートする
	 * @param dishDTO
	 * @return
	 */
	void update(DishDTO dishDTO);
	
	
	 /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);


    /**
     * dishを販売開始か停止かにする
     * @param status
     * @param id 
     */
	void startOrStop(Integer status, Long id);


	/**
	 * 根据分类id查询菜品
	 *
	 * @param categoryId
	 * @return
	 */
    List<Dish> list(Long categoryId);
}








