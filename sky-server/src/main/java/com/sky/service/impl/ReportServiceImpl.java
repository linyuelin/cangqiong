package com.sky.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;



@Service
public class ReportServiceImpl implements ReportService {
	
	
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	
	@Autowired
	private WorkspaceService workspaceService ;
	
	
	/**
	 * 業績統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
		
		List<LocalDate> dateList =new ArrayList();
		dateList.add(begin);
		
		while(!begin.equals(end)) {
			begin = begin.plusDays(1);
			dateList.add(begin);
		};
		 
		List<Double> turnoverList =  new ArrayList();
		for (LocalDate date : dateList) {
			
			LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN );
			LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
			
			Map map =new HashMap();
			map.put("begin", beginTime );
			map.put("end", endTime );
			map.put("status", Orders.COMPLETED);
			
			Double turnover =orderMapper.sumByMap(map);
			turnover =turnover ==null ? 0.0:turnover;
			turnoverList.add(turnover);
			
		}
		
		return TurnoverReportVO
				 .builder()
				.dateList(StringUtils.join(dateList,","))
				.turnoverList(StringUtils.join(turnoverList,","))
			    .build();
	}

	/**
	 * ユーザー統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
		
		List<LocalDate> dateList =new ArrayList();
		dateList.add(begin);
		while(!begin.equals(end)) {
			begin = begin.plusDays(1);
			dateList.add(begin);
		}
		
		List<Integer> newUserList =new ArrayList();
		List<Integer> totalUserList =new ArrayList();
		for (LocalDate date : dateList) {
			
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
			
			Map map =new HashMap();
			map.put("end", endTime);
			Integer countUser = userMapper.countByMap(map);
			
			map.put("begin", beginTime);
			Integer newUser = userMapper.countByMap(map);
			
			totalUserList.add(countUser);
			
			newUserList.add(newUser);
		}
		
		return UserReportVO
				 .builder()
				.dateList(StringUtils.join(dateList,","))
				.totalUserList(StringUtils.join(totalUserList,","))
				.newUserList(StringUtils.join(newUserList,","))
			    .build();
	}



	/**
	 * オーダー統計をセレクトする
	 * @param begin
	 * @param end
	 * @return
	 */
	@Override
	public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
		
		List<LocalDate> dateList = new ArrayList();
		dateList.add(begin);
		while(! begin.equals(end)) {
			begin = begin.plusDays(1);
			dateList.add(begin);
		}
		
		List<Integer> orderCountList = new ArrayList();
		List<Integer> validOrderList = new ArrayList();
		for (LocalDate date : dateList) {
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
			
			Map map =new HashMap();
			map.put("begin", beginTime);
			map.put("end", endTime);
			
		   Integer orderCount =orderMapper.countByMap(map);
		   
		   map.put("status", Orders.COMPLETED);
		   
		   Integer validOrder =orderMapper.countByMap(map);
		   
		   orderCountList .add(orderCount);
		   validOrderList.add(validOrder);
		   
		}
		  //例の時間帯のオーダー総数
		  Integer totalOrderCount= orderCountList.stream().reduce(Integer::sum).get();
		  //例の時間帯の有効なオーダーのまとめ
		  Integer validOrderCount= validOrderList.stream().reduce(Integer::sum).get();
		  
		  Double orderCompletionRate = 0.0;
		  if(totalOrderCount != 0) {
			  orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount;
		  }
		  
		 
		return OrderReportVO.builder()
		          .dateList(StringUtils.join(dateList,","))
		          .orderCountList(StringUtils.join(orderCountList,","))
		          .validOrderCountList(StringUtils.join(validOrderList,","))
		          .totalOrderCount(totalOrderCount)
		          .validOrderCount(validOrderCount)
		          .orderCompletionRate(orderCompletionRate)
		          .build();
	}

	
	
	@Override
	public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
		LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
		LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
		List<GoodsSalesDTO> salesTOP10 =orderMapper.getTop10(beginTime ,endTime);
		
		List<String> names =salesTOP10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
		String nameList = StringUtils.join(names,",");
		
		List<Integer> numbers =salesTOP10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
		String numberList = StringUtils.join(numbers,",");
		

		
		
		return SalesTop10ReportVO.builder().nameList(nameList).numberList(numberList).build();
	}

	/**
	 * 営業状況表をエクスポート
	 * @param response
	 */
	public void exportBusinessData(HttpServletResponse response) {
		
		//データーベースにアクセスして、ここ三十日の業績を調べる
		LocalDate dateBegin = LocalDate.now().minusDays(30);
		LocalDate dateEnd = LocalDate.now().minusDays(1);
		
		
	    BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN),LocalDateTime.of(dateEnd, LocalTime.MAX));
	    //POIでExcelファイルに書き込む
	    InputStream in = this.getClass().getResourceAsStream("/template/yunyingbaobiao.xlsx");
	    
	    try {
	    	//サンプルを基づいて、Excelファイルを作る
	    	XSSFWorkbook excel = new XSSFWorkbook(in);
	    	
	    	//sheetを取得する
	    	XSSFSheet sheet = excel.getSheet("sheet1");
	    	
	    	//データを書き込む　時間
	    	sheet.getRow(1).getCell(1).setCellValue("時間:" +dateBegin +"~"+dateEnd);
	    	
	    	//第四行を取得する
	    	XSSFRow row = sheet.getRow(3);
	    	row.getCell(2).setCellValue(businessDataVO.getTurnover());
	    	row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
	    	row.getCell(6).setCellValue(businessDataVO.getNewUsers());
	    	
	    	//第五行を取得して書き込む
	    	row = sheet.getRow(4);
	    	row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
	    	row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
	        
	    	//明細データを書き込む
	    	for (int i = 0; i < 30; i++) {
				LocalDate date = dateBegin.plusDays(i);
				BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date,LocalTime.MAX));
			   
				//書き込む　
				row = sheet.getRow(7 + i);
		    	row.getCell(1).setCellValue(date.toString());
		    	row.getCell(2).setCellValue(businessData.getTurnover());
		    	row.getCell(3).setCellValue(businessData.getValidOrderCount());
		    	row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
		    	row.getCell(5).setCellValue(businessData.getUnitPrice());
		    	row.getCell(6).setCellValue(businessData.getNewUsers());
		    	
	    	}
	    	
	    	
	    	
	    	//出力ストリームでファイルを
	    	ServletOutputStream out = response.getOutputStream();
	    	excel.write(out);
	    	
	    	
	    	//リソースを閉じる
	    	out.close();
	    	excel.close();
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
	}

	

}






