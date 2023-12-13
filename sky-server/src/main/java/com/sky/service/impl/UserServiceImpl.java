package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	
	//wechatサーバーアドレス
	public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
	
	@Autowired
	private WeChatProperties weChatProperties;
	
	@Autowired
	private UserMapper userMapper;
     
/**
 * wechatログイン
 * @param userLoginDTO
 * @return
 */
	public User wxLogin(UserLoginDTO userLoginDTO) {
		 //wechatサーバーをアクセスして、openidを取得する
		 String openid = getOpenid(userLoginDTO);
		//openidはヌル値だったら、ログイン失敗、エラーをスローする
		 if(openid == null) {
			 throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
		 }
		//新規ユーザーかどうかを判断する
		User user = userMapper.getByOpenid(openid);
       //新規ユーザーだったら、自動登録
		if(user == null) {
			user = User.builder()
					.openid(openid)
					.createTime(LocalDateTime.now())
					.build();
			userMapper.insert(user);
		}
		//userオブジェクトを返えす
		return user;
	}

	/**
	 * wechatサーバーをアクセスして、openidを取得する
	 * @param userLoginDTO
	 * @return
	 */
private String getOpenid(UserLoginDTO userLoginDTO) {
	Map<String , String > map = new HashMap<>();
	 map.put("appid", weChatProperties.getAppid());
	 map.put("secret", weChatProperties.getSecret());
	 map.put("js_code", userLoginDTO.getCode());
	 map.put("grant_type", "authorization_code");
	 String json = HttpClientUtil.doGet(WX_LOGIN, map);
	 
	 JSONObject jsonObject = JSON.parseObject(json);
	 String openid = jsonObject.getString("openid");
	return openid;
}
       
	
}
