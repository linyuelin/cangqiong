package com.sky.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;


import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.omg.CORBA.portable.InputStream;

public class POITest {

	
	public static void write() throws FileNotFoundException, IOException {

		// キャッシューの中にExcelファイルを作る
		XSSFWorkbook excel = new XSSFWorkbook();

		// Excelファイルの中にSheetページを
		XSSFSheet sheet = excel.createSheet("info");

		// sheetの中に行っていうオブジェクトを,0から
		XSSFRow row = sheet.createRow(1);

		// 格を作って、書き込む
		row.createCell(1).setCellValue("名前");
		row.createCell(2).setCellValue("町");

		// 改行する
		row = sheet.createRow(2);
		row.createCell(1).setCellValue("川端成康");
		row.createCell(2).setCellValue("新潟");

		// 改行する
		row = sheet.createRow(2);
		row.createCell(1).setCellValue("渡邊雄太");
		row.createCell(2).setCellValue("ニューヨーク");
		
		FileOutputStream out =	new FileOutputStream(new File("/Users/dreamtank/info.xlsx"));
        
		excel.write(out);
		excel.close();
		out.close();
		
		
	}

	public static void read()throws FileNotFoundException, IOException{
		FileInputStream in =new FileInputStream(new File("/Users/dreamtank/info.xlsx"));
		
		//Excelファイイルを読みとる
		XSSFWorkbook  excel = new XSSFWorkbook(in);
		//中のSheetを取得する
		XSSFSheet sheet = excel.getSheetAt(0);
		//書き込んでいる最後の一行の番号を
		int lastRowNum = sheet.getLastRowNum();
		for (int i = 1; i <= lastRowNum; i++) {
			//一行目
			XSSFRow row = sheet.getRow(i);
			//格の内容を取得する
			String cellValue1 = row.getCell(1).getStringCellValue();
			String cellValue2 = row.getCell(2).getStringCellValue();
			System.out.println(cellValue1 +" "+cellValue2);
			
		}
		in.close();
		excel.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
//       write();
       read();
	}

}
