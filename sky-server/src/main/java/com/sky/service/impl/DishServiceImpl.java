package com.sky.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import lombok.extern.slf4j.Slf4j;

/**
 * メニュー業務層
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

	@Autowired
	private DishMapper dishMapper;

	@Autowired
	private DishFlavorMapper dishFlavorMapper;

	@Autowired
	private SetmealDishMapper setmealDishMapper;
	
	@Autowired
	private SetmealMapper setmealMapper;

	@Transactional
	public void saveWithFlavor(DishDTO dishDTO) {

		// dishのデータ一つ入れる
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		dishMapper.insert(dish);

		// dishId
		Long dishId = dish.getId();

		// DishFlavorのデータ何個入れる
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && flavors.size() > 0) {
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId);

			});
			dishFlavorMapper.insertBatch(flavors);
		}
	}

	/**
	 * メニューのページング検索
	 * 
	 * @param dishPageQueryDTO
	 * @return
	 */
	public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
		PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
		Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * メニューの一括削除
	 * 
	 * @param ids
	 * @return
	 */
	@Transactional
	public void deleteById(List<Long> ids) {
		for (Long id : ids) {
			// ステータスの確認
			Dish dish = dishMapper.getById(id);
			if (dish.getStatus() == StatusConstant.ENABLE) {
				// 料理は売っているので、削除できません
				throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
			}
		}
		// 定食に含まれてるかどうか
		List<Long> setmealDishIds = setmealDishMapper.getSetmealDishIdsByDishIds(ids);
		if (setmealDishIds != null && setmealDishIds.size() > 0) {
			throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
		}

		// 削除
//		 for (Long id : ids) {
//			 dishMapper.deleteById(id);
//			 
//			//対応する味を削除
//			 dishFlavorMapper.deleteByDishId(id);
//		}

		// idの集合に基づいて料理を削除する
		dishMapper.deleteByIds(ids);
		// 対応する味を削除
		dishFlavorMapper.deleteByDishIds(ids);

	}

	/**
	 * idによってセレクト
	 * 
	 * @param id
	 * @return
	 */
	public DishVO getById(Long id) {

		// dish
		Dish dish = dishMapper.getById(id);

		DishVO dishVO = new DishVO();
		BeanUtils.copyProperties(dish, dishVO);

		// DishFlavor
		List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(id);
		dishVO.setFlavors(dishFlavor);

		return dishVO;
	}

	/**
	 * 料理の情報をアップデートする
	 * 
	 * @param dishDTO
	 * @return
	 */
	public void update(DishDTO dishDTO) {
		// dish基本情報更新
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		dishMapper.update(dish);
		// 元の味を削除する
		dishFlavorMapper.deleteByDishId(dishDTO.getId());

		// 新しいのを入れる
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && flavors.size() > 0) {
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishDTO.getId());

			});
			dishFlavorMapper.insertBatch(flavors);
		}

	}

	/**
	 * 条件查询菜品和口味
	 * 
	 * @param dish
	 * @return
	 */
	public List<DishVO> listWithFlavor(Dish dish) {
		List<Dish> dishList = dishMapper.list(dish);

		List<DishVO> dishVOList = new ArrayList<>();

		for (Dish d : dishList) {
			DishVO dishVO = new DishVO();
			BeanUtils.copyProperties(d, dishVO);

			// 根据菜品id查询对应的口味
			List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

			dishVO.setFlavors(flavors);
			dishVOList.add(dishVO);
		}

		return dishVOList;
	}

	/**
	 * dishを販売開始か停止かにする
	 * 
	 * @param status
	 */
	@Transactional
	public void startOrStop(Integer status, Long id) {
		// ステータスを変える

		Dish dish = Dish.builder().id(id).status(status).build();

		dishMapper.update(dish);

		// もし販売停止にしたら、それに伴う定食も停止する
		
		 if(status == StatusConstant.DISABLE) {
			 ArrayList<Long> dishIds = new ArrayList<>();
			 dishIds.add(id);
			 List<Long> setmealIds = setmealDishMapper.getSetmealDishIdsByDishIds(dishIds);
			 if(setmealIds != null&& setmealIds.size()>0) {
			 for (Long setmealId : setmealIds) {
				  Setmeal setmeal =Setmeal.builder()
						  .id(setmealId)
						  .status(StatusConstant.DISABLE)
						  .build();
				  setmealMapper.update(setmeal);
			}
			 }
		 }
		 

	}

	/**
	 * 根据分类id查询菜品
	 *
	 * @param categoryId
	 * @return
	 */
	public List<Dish> list(Long categoryId) {
		Dish dish = Dish.builder()
				.categoryId(categoryId)
				.status(StatusConstant.ENABLE)
				.build();
		return dishMapper.list(dish);
	}


}








