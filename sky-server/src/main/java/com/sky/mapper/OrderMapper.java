package com.sky.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;

@Mapper
public interface OrderMapper {

	/**
	 * オーダーデータをordersに入れる
	 * @param orders
	 */
	void insert(Orders orders);
	
	 /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 注文のステータスと注文時間でオーダーをセレクトする
     * @param pendingPayment
     * @param time
     */
    @Select("select * from orders where status = #{pendingPayment} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime time);

    /**
     * mapによって業績をセレクトする
     * @param map
     * @return
     */
	Integer orderByMap(Map map);

	
	/**
	 * 業績統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	Double sumByMap(Map map);
	

	 /** 
	  * トップテン
	  * @param beginTime
	  * @param endTime
	  * @return
	  */
	List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime);


}






