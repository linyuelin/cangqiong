package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;

import ch.qos.logback.core.joran.util.beans.BeanUtil;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;

	/**
	 * 员工登录
	 *
	 * @param employeeLoginDTO
	 * @return
	 */
	public Employee login(EmployeeLoginDTO employeeLoginDTO) {
		String username = employeeLoginDTO.getUsername();
		String password = employeeLoginDTO.getPassword();

		// 1、根据用户名查询数据库中的数据
		Employee employee = employeeMapper.getByUsername(username);

		// 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
		if (employee == null) {
			// 账号不存在
			throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
		}

		// 密码比对
		//Md5処理
		password = DigestUtils.md5DigestAsHex(password.getBytes());
		if (!password.equals(employee.getPassword())) {
			// 密码错误
			throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
		}

		if (employee.getStatus() == StatusConstant.DISABLE) {
			// 账号被锁定
			throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
		}

		// 3、返回实体对象
		return employee;
	}

	/**
     * 社員追加
     * @param employeeDTO
     */
	@Override
	public void save(EmployeeDTO employeeDTO) {
		Employee employee = new  Employee();
		
		//プロパティーをコピーする
		BeanUtils.copyProperties(employeeDTO, employee);
		
		//アカウントのステータスを設定する
		employee.setStatus(StatusConstant.ENABLE);
		
		//パスワード追加
		employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
		
		//クリエート時間・アップデート時間
//		employee.setCreateTime(LocalDateTime.now());
//		employee.setUpdateTime(LocalDateTime.now());
		
		//クリエートする方・アップデート方
	
//		employee.setCreateUser(BaseContext.getCurrentId());
//		employee.setUpdateUser(BaseContext.getCurrentId());
		
		employeeMapper.insert(employee);
		
		
	}

	/**
	 * ページング検索
	 * @param employeePageQueryDT
	 * @return
	 */
	public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDT) {
		//ページング検索をスタート（PageHelperプラグインは自動的にページ条件を計算する）
		PageHelper.startPage(employeePageQueryDT.getPage(),employeePageQueryDT.getPageSize());
		Page<Employee> page =employeeMapper.pageQuery(employeePageQueryDT);
		long total =page.getTotal();
		List<Employee> records =page.getResult();
		
		return new PageResult(total,records);
	}


    /**
     * アカウントを有効か無効にする
     * @param status
     * @param id
     * @return
     */
	public void startOrStop(Integer status, Long id) {
		Employee employee = new Employee();
		employee.setStatus(status);
	    employee.setId(id);	
	    
	    employeeMapper.update(employee);
	}

	@Override
	public Employee getById(Long id) {
		Employee employee = employeeMapper.getById(id);
		return employee;
	}

	@Override
	public void update(EmployeeDTO employeeDTO) {
		Employee employee = new Employee();
		//パターンのコピー
		BeanUtils.copyProperties(employeeDTO, employee);
		
		//操作する方とアップデート時間
//		employee.setUpdateTime(LocalDateTime.now());
//		employee.setUpdateUser(BaseContext.getCurrentId());
		
		employeeMapper.update(employee);
		
	}

}






