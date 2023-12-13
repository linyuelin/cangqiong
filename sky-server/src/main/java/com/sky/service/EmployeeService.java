package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 社員追加
     * @param employeeDTO
     */
	void save(EmployeeDTO employeeDTO);
     
	/**
	 * ページング検索
	 * @param employeePageQueryDT
	 * @return
	 */
	PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDT);


    /**
     * アカウントを有効か無効にする
     * @param status
     * @param id
     * @return
     */
	void startOrStop(Integer status, Long id);

	
	/**
	 * id によってセレクトする
	 * @param id
	 * @return
	 */
	Employee getById(Long id);

	
	/**
	 * 社員情報をアップデートする
	 * @param employeeDTO
	 * @return
	 */
	void update(EmployeeDTO employeeDTO);
     
}

