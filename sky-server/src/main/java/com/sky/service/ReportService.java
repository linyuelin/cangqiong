package com.sky.service;

import java.time.LocalDate;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

public interface ReportService {

	
	/**
	 * 業績統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
	
     
	/**
	 * ユーザー統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	UserReportVO getUserStatistics(LocalDate begin, LocalDate end);



	/**
	 * オーダー統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
	
	

}
