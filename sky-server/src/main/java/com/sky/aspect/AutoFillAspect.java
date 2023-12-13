package com.sky.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice.Local;

/**
 * カスタムアスペクト、公共フィールドを自動的に入れるロジックを実現する
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
   /*
    * 切り込み点
    */
	
	@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
	public void autoFillPointCut() {}
	
	
	/**
	 * 事前的に公共フィールドを入れる
	 */
	@Before("autoFillPointCut()")
	public void autoFill(JoinPoint joinPoint) {
		log.info("スタート");
		
		//データーベース操作のメソッドをゲットする
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
		OperationType operationType = autoFill.value();
		
		//インターセプト方法のエンティティーのパターン
		Object[] args = joinPoint.getArgs();
		if(args == null || args.length == 0) {
			return;
		}
		Object entity =args[0];
		
		//代入データのゲット
		LocalDateTime now = LocalDateTime.now();
		Long currentId = BaseContext.getCurrentId();
		
		//データーベースの操作メソッド別にデータを代入
		if(operationType == OperationType.INSERT) {
			try {
			Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
			Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
			Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
			Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
			
			//オブジェクトのプロパティーにリフレクションで値を設定する
			setCreateTime.invoke(entity, now);
			setCreateUser.invoke(entity, currentId);
			setUpdateTime.invoke(entity, now);
			setUpdateUser.invoke(entity, currentId);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}else if(operationType == OperationType.UPDATE) {
			try {
				Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
				Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
				
				//オブジェクトのプロパティーにリフレクションで値を設定する
				setUpdateTime.invoke(entity, now);
				setUpdateUser.invoke(entity, currentId);
				}catch(Exception e) {
					e.printStackTrace();
				}
		}
	}
}








