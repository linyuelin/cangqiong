package com.sky.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.sky.entity.OrderDetail;

@Mapper
public interface OrderDetailMapper {
	
	/**
	 * orderDetailを入れる
	 * @param arrayList
	 */

	void insertBatch(ArrayList<OrderDetail> arrayList);
    
	
	
}
