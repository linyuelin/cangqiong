package com.sky.task;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyTask {
  
//	@Scheduled(cron ="0/5 * * * * ?")
	public void executeTask() {
		log.info("タイマー開始: {}",new Date());
	}
}
