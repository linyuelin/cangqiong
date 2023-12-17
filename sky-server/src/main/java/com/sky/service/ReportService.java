package com.sky.service;

import java.time.LocalDate;

import com.sky.vo.TurnoverReportVO;

public interface ReportService {

	
	/**
	 * 業績統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
	
	

}
