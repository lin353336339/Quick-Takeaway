package com.Quick.service;

import com.Quick.dto.UserLoginDTO;
import com.Quick.entity.User;
import com.Quick.vo.BusinessDataVO;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface UserService {
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
