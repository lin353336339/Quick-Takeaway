package com.Quick.controller.admin;

import com.github.pagehelper.Page;
import com.Quick.constant.JwtClaimsConstant;
import com.Quick.dto.EmployeeDTO;
import com.Quick.dto.EmployeeLoginDTO;
import com.Quick.dto.EmployeePageQueryDTO;
import com.Quick.dto.PasswordEditDTO;
import com.Quick.entity.Employee;
import com.Quick.properties.JwtProperties;
import com.Quick.result.PageResult;
import com.Quick.result.Result;
import com.Quick.service.EmployeeService;
import com.Quick.utils.JwtUtil;
import com.Quick.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Api(tags = "员工相关接口")
@Slf4j
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
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退�?
     *
     * @return
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     * @return
     */
    @ApiOperation("新增员工")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询，页码：{}，页大小：{}", employeePageQueryDTO);
        PageResult page = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(page);
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用员工账号")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工账号：{}，员工id：{}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @ApiOperation("根据id查询员工")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 根据id修改员工账号密码
     * @param passwordEditDTO
     * @return
     */
    @ApiOperation("根据id修改员工账号密码")
    @PutMapping("/editPassword")
    public Result UpdatePassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("根据id修改员工账号密码：{}", passwordEditDTO);
        employeeService.UpdatePassword(passwordEditDTO);
        return Result.success();
    }

    /**
     * 根据id删除员工
     * @param id
     * @return
     */
    @ApiOperation("根据id删除员工")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        log.info("根据id删除员工：{}", id);
        employeeService.deleteById(id);
        return Result.success();
    }

}
