package com.sky.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderTask {
	
	@Autowired
	private OrderMapper orderMapper;
	
    @Scheduled(cron ="0 * * * * * " )//一分ごとに作動する
	public void procesTimeoutOrder() {
		log.info("タスクスケジューリングはタイムアウトのオーダーをキャンセルする　{}",LocalDateTime.now());
    
    	LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
    	List<Orders> orderLists = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);
    	
    	if(orderLists != null && orderLists.size() > 0) {
    		
    	for (Orders orders : orderLists) {
			 orders.setStatus(Orders.CANCELLED);
			 orders.setCancelReason("タイムアウト");
			 orders.setCancelTime(LocalDateTime.now());
			 orderMapper.update(orders);
			 }
    	}
    	
	}
    
    @Scheduled(cron = "0 0 1 * * ?")//毎日一時に作動する
//    @Scheduled(cron ="0/5 * * * * ?")
    public void processDeliveryOrder() {
    	log.info("タスクスケジューリングは配送中のオーダーを処理する");
    	LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
    	List<Orders> orderLists  =orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

    	if(orderLists != null && orderLists.size() > 0) {
    		
    	for (Orders orders : orderLists) {
			 orders.setStatus(Orders.COMPLETED);
			 orderMapper.update(orders);
			 }
    }
	  
    }
}






