package com.Quick.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.Quick.constant.MessageConstant;
import com.Quick.dto.UserLoginDTO;
import com.Quick.entity.User;
import com.Quick.exception.LoginFailedException;
import com.Quick.mapper.UserMapper;
import com.Quick.properties.WeChatProperties;
import com.Quick.service.UserService;
import com.Quick.utils.HttpClientUtil;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //微信登录接口
    static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties  weChatProperties;

    /**
     * 微信登录
     * @Param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        //调用微信接口实现登录
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空则说明登录失败，抛出业务异�?
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断用户是否存在，如果不存在则自动注�?
        User user = userMapper.getByOpenid(openid);
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回用户对象
        return user;
    }

    /**
     * 调用微信接口实现登录
     * @param code
     * @return
     */
    public String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String  json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        JSONObject jsonObject = JSONObject.parseObject(json);

       String openid = jsonObject.getString("openid");
       return openid;
    }
}
