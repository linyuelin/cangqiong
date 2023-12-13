package com.sky.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserLoginVO.UserLoginVOBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/user")
@Api(tags ="ユーザー側のインターフェース")
@Slf4j
public class UserController {
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtProperties jwtProperties;
	
	/**
	 * wechatログイン
	 * @param userLoginDTO
	 * @return
	 */
	@PostMapping("/login")
	@ApiOperation("wechatログイン")
	public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
		log.info("weixinユーザーログイン: {}",userLoginDTO.getCode());
		//ログイン
		User user =userService.wxLogin(userLoginDTO);
		
		//JWTトークンを作る
		Map<String,Object>claims = new HashMap<>();
		claims.put(JwtClaimsConstant.USER_ID, user.getId());
		String token =JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
		UserLoginVO userLoginVO = UserLoginVO.builder()
		     .id(user.getId())
		     .openid(user.getOpenid())
		     .token(token)
		     .build();
		
		return Result.success(userLoginVO);
	}
}





