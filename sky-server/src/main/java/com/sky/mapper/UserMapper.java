package com.sky.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.User;

@Mapper
public interface UserMapper {
	
    
	/**
	 * openidによってユーザーをセレクトする
	 * @param openid
	 * @return
	 */
	@Select("select * from user where openid = #{openid}")
	User getByOpenid(String openid);

	
	/**
	 * 新規ユーザーをインサート
	 * @param user
	 */
	void insert(User user);


	@Select ("select * from user where id = #{id}")
	User getById(Long userId);

	

	/**
	 * mapによって総ユーザ数と新規ユーザーをまとめる
	 * @param map
	 * @return
	 */
	Integer countByMap(Map map);

	
	
	
	
	 
	
}
