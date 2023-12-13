package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;

import lombok.extern.slf4j.Slf4j;

@Mapper
public interface DishMapper {

	/**
	 * 根据分类id查询菜品数量
	 * 
	 * @param categoryId
	 * @return
	 */
	@Select("select count(id) from dish where category_id = #{categoryId}")
	Integer countByCategoryId(Long categoryId);

	/**
	 * 料理の種類増加
	 * 
	 * @param dish
	 * @return
	 */
	@AutoFill(value = OperationType.INSERT)
	void insert(Dish dish);

	/**
	 * メニューのページング検索
	 * 
	 * @param dishPageQueryDTO
	 * @return
	 */
	Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

	/**
	 * id によってセレクトする
	 * 
	 * @param id
	 * @return
	 */
	@Select("select * from dish where id = #{id}")
	Dish getById(Long id);

	/**
	 * id によって削除する
	 * 
	 * @param id
	 */
	@Delete("delete from dish where id = #{id}")
	void deleteById(Long id);

	/**
	 * idの集合に基づいて料理を削除する
	 * 
	 * @param ids
	 */
	void deleteByIds(List<Long> ids);

	/**
	 * dish情報更新
	 * 
	 * @param dish
	 */
	@AutoFill(value = OperationType.UPDATE)
	void update(Dish dish);

	/**
	 * 动态条件查询菜品
	 * 
	 * @param dish
	 * @return
	 */
	List<Dish> list(Dish dish);

	/**
	 * 根据套餐id查询菜品
	 * @param setmealId
	 * @return
	 */
	@Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
	List<Dish> getBySetmealId(Long setmealId);

}
