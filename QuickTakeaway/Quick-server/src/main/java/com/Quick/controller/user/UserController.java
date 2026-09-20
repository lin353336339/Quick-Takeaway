package com.Quick.controller.user;

import com.Quick.constant.JwtClaimsConstant;
import com.Quick.dto.UserLoginDTO;
import com.Quick.entity.User;
import com.Quick.properties.JwtProperties;
import com.Quick.result.Result;
import com.Quick.service.UserService;
import com.Quick.utils.JwtUtil;
import com.Quick.vo.BusinessDataVO;
import com.Quick.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端用户相关接")
public class UserController {

    @Autowired
    UserService  userService;

    @Autowired
    JwtProperties jwtProperties;
    /**
     * 微信登录
     * @Param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录：{}", userLoginDTO);

        //微信登录
        User user = userService.wxLogin(userLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }


}
