package com.Quick.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.Quick.constant.EmployeeConstant;
import com.Quick.constant.JwtClaimsConstant;
import com.Quick.constant.MessageConstant;
import com.Quick.constant.StatusConstant;
import com.Quick.context.BaseContext;
import com.Quick.dto.EmployeeDTO;
import com.Quick.dto.EmployeeLoginDTO;
import com.Quick.dto.EmployeePageQueryDTO;
import com.Quick.dto.PasswordEditDTO;
import com.Quick.entity.Employee;
import com.Quick.exception.AccountLockedException;
import com.Quick.exception.AccountNotFoundException;
import com.Quick.exception.DeletionNotAllowedException;
import com.Quick.exception.PasswordErrorException;
import com.Quick.mapper.EmployeeMapper;
import com.Quick.properties.JwtProperties;
import com.Quick.result.PageResult;
import com.Quick.result.Result;
import com.Quick.service.EmployeeService;
import com.Quick.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

        //1、根据用户名查询数据库中的数�?
        Employee employee = employeeMapper.getByUsername(username);

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定�?
        if (employee == null) {
            //账号不存�?
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁�?
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对�?
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Transactional
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //验证当前用户是否是管理员
        if (BaseContext.getCurrentId() != EmployeeConstant.AdminId){
            return;
        }

        Employee employee = new Employee();

        //对象属性拷�?
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置账号状�?
        employee.setStatus(StatusConstant.ENABLE);


        //保存到数据库
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查�?
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //条件查询
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        //封装结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());

        return pageResult;
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Transactional
    @Override
    public void startOrStop(Integer status, Long id) {
        //验证当前用户是否是管理员
        if (BaseContext.getCurrentId() != EmployeeConstant.AdminId){
            return;
        }

        //设置修改时间
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        //根据id查询员工信息
        Employee employee = employeeMapper.getById(id);
        //保护密码隐私�?
        employee.setPassword("bu-xu-kan");
        return employee;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    @Transactional
    @Override
    public void update(EmployeeDTO employeeDTO) {
        //验证当前用户是否是管理员
        if (BaseContext.getCurrentId() != EmployeeConstant.AdminId){
            return ;
        }

        //对象属性拷�?
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employeeMapper.update(employee);
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    @Override
    public void UpdatePassword(PasswordEditDTO passwordEditDTO) {
        log.info("修改密码：{}", passwordEditDTO);
        //根据id查询员工信息
        Employee employee = employeeMapper.getById(BaseContext.getCurrentId());

        String  password = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        //判断原密码是否正�?
        //修改密码
        if (employee != null && employee.getPassword().equals(password)){
            employee.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
            employeeMapper.update(employee);
        }else {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
    }

    /**
     * 删除员工
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        //验证当前用户是否是管理员
        if (BaseContext.getCurrentId() != EmployeeConstant.AdminId){
            return ;
        }
        Employee employee = employeeMapper.getById(id);
        if (employee.getStatus() == StatusConstant.ENABLE){
            throw new DeletionNotAllowedException(MessageConstant.DELETE_DISABLE_STAFF_ERROR);
        }
        employeeMapper.deleteById(id);
    }


}
