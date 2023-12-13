package com.sky.controller.admin;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 料理の種類管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "料理の種類管理")
@Slf4j
public class DishController {
	
	@Autowired
	private DishService dishService ;
	
	@Autowired
	private RedisTemplate redisTemplate ;
	
	
    
    
	
	/**
	 * 料理の種類増加
	 * @param dishDTO
	 * @return
	 */
	@PostMapping
	@ApiOperation("料理の種類増加")
	public Result save(@RequestBody DishDTO dishDTO) {
		dishService.saveWithFlavor(dishDTO);
		String key ="dish" + dishDTO.getCategoryId();
		cleanCache(key);
		return Result.success();
	}
	

	/**
	 * メニューのページング検索
	 * @param dishPageQueryDTO
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("ページング検索")
	public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
		log.info("ページング検索 : {}", dishPageQueryDTO);
		PageResult pageResult =dishService.pageQuery(dishPageQueryDTO);
		return Result.success(pageResult);
	}
	
	/**
	 * メニューの削除
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	@ApiOperation("メニューの削除")
	public Result delete(@RequestParam List<Long> ids) {
		log.info("メニューの削除: {} ",ids);
		dishService.deleteById(ids);
		cleanCache("dish*");
		return Result.success();
	}
	
	/**
	 * idによってセレクト
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@ApiOperation("idによってセレクト")
	public Result<DishVO> getById(@PathVariable Long id){
		log.info("idによってセレクト: {}",id);
		DishVO dishVO = dishService.getById(id);
		return Result.success(dishVO);
	}
	
	/**
	 * 料理の情報をアップデートする
	 * @param dishDTO
	 * @return
	 */
	@PutMapping
	@ApiOperation("料理の情報をアップデートする")
	public Result update(@RequestBody DishDTO dishDTO) {
		dishService.update(dishDTO);
		cleanCache("dish*");
		return Result.success();
	}
	
	/**
	 * dishを販売開始か停止かにする
	 * @param status
	 * @param id
	 * @return
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("dishを販売開始か停止かにする")
	public Result startOrStop(@PathVariable Integer status,Long id) {
		dishService.startOrStop(status,id);
		cleanCache("dish*");
		return Result.success();
		
	}
	
	/**
	 * キャッシュを削除する
	 * @param pattern
	 */
	private void cleanCache(String pattern) {
	Set keys = redisTemplate.keys(pattern);
	redisTemplate.delete(keys);
		 
	}

	/**
	 * 根据分类id查询菜品
	 *
	 * @param categoryId
	 * @return
	 */
	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品")
	public Result<List<Dish>> list(Long categoryId) {
		List<Dish> list = dishService.list(categoryId);
		return Result.success(list);
	}
	
	
	
}







