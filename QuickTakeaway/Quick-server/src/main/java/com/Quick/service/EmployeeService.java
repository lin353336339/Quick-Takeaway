package com.Quick.service;

import com.github.pagehelper.Page;
import com.Quick.dto.EmployeeDTO;
import com.Quick.dto.EmployeeLoginDTO;
import com.Quick.dto.EmployeePageQueryDTO;
import com.Quick.dto.PasswordEditDTO;
import com.Quick.entity.Employee;
import com.Quick.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据id查询员工
     * @param id
     * @param status
     * @return
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 根据id修改员工账号密码
     * @param passwordEditDTO
     */
    void UpdatePassword(PasswordEditDTO passwordEditDTO);

    /**
     * 根据id删除员工
     * @param id
     */
    void deleteById(Long id);
}
