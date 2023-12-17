package com.sky.controller.admin;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "データ統計")
@Slf4j
public class ReportController {
	
	@Autowired
    private ReportService reportService;
	
	
	@GetMapping("/turnoverStatistics")
	@ApiOperation("業績統計")
	public Result<TurnoverReportVO> turnoverStatistics( 
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
		log.info("業績統計のコントローラーで取得したデータ {} {}",end,begin);
		TurnoverReportVO turnoverReportVO =reportService.getTurnoverStatistics(begin,end);
		return Result.success(turnoverReportVO);
	}
	
	@GetMapping("/userStatistics")
	@ApiOperation("ユーザー統計")
	public Result<UserReportVO> userStatistics(
			@DateTimeFormat(pattern = "yyyy-MM-dd" )LocalDate begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
			){
		log.info("ユーザー統計のコントローラーで取得したデータ {} {}",end,begin);
		UserReportVO userReportVO =reportService.getUserStatistics(begin,end);
		return Result.success(userReportVO);
		
	}
}





