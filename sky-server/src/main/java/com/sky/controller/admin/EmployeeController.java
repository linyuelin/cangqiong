package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "スタッフについてのインターフェース")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private JwtProperties jwtProperties;

	/**
	 * 登录
	 *
	 * @param employeeLoginDTO
	 * @return
	 */
	@PostMapping("/login")
	@ApiOperation(value = "社員登録")
	public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
		log.info("员工登录：{}", employeeLoginDTO);

		Employee employee = employeeService.login(employeeLoginDTO);

		// 登录成功后，生成jwt令牌
		Map<String, Object> claims = new HashMap<>();
		claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
		String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

		EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder().id(employee.getId())
				.userName(employee.getUsername()).name(employee.getName()).token(token).build();

		return Result.success(employeeLoginVO);
	}

	/**
	 * 退出
	 *
	 * @return
	 */
	@PostMapping("/logout")
	@ApiOperation("社員ログアウト")
	public Result<String> logout() {
		return Result.success();
	}

	/**
	 * 社員追加
	 * 
	 * @param employeeDTO
	 * @return
	 */
	@PostMapping
	@ApiOperation("社員追加")
	public Result save(@RequestBody EmployeeDTO employeeDTO) {
		log.info("社員追加: {}", employeeDTO);
		employeeService.save(employeeDTO);
		return Result.success();
	}

	/**
	 * ページング検索
	 * 
	 * @param employeePageQueryDT
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("ページング検索")
	public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDT) {
		log.info("ページング検索 : {}", employeePageQueryDT);
		PageResult pageResult = employeeService.pageQuery(employeePageQueryDT);
		return Result.success(pageResult);
	}

	/**
	 * アカウントを有効か無効にする
	 * 
	 * @param status
	 * @param id
	 * @return
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("アカウントを有効か無効にする")
	public Result startOrStop(@PathVariable Integer status, Long id) {
		log.info("アカウントを有効か無効にする :{},{}", status, id);
		employeeService.startOrStop(status, id);
		return Result.success();
	}

	/**
	 * idによってセレクトする
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@ApiOperation("idによってセレクトする")
	public Result<Employee> getById(@PathVariable Long id) {
		Employee employee = employeeService.getById(id);
		return Result.success(employee);
	}
	
	/**
	 * 社員情報をアップデートする
	 * @param employeeDTO
	 * @return
	 */
	@PutMapping
	@ApiOperation("社員情報をアップデートする")
	public Result update(@RequestBody EmployeeDTO employeeDTO) {
		employeeService.update(employeeDTO);
		return Result.success();
	}

}
