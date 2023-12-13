package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {
	

	/**
	 * wechatログイン
	 * @param userLoginDTO
	 * @return
	 */
	  User wxLogin(UserLoginDTO userLoginDTO);

}
