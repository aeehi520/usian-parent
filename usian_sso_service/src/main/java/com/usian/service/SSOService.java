package com.usian.service;

import com.usian.pojo.TbUser;

import java.util.Map;

/**
 * @ClassName : SSOService
 * @Author : lenovo
 * @Date: 2021/1/27 10:01
 */
public interface SSOService {
    Boolean checkUserInfo(String checkValue, Integer checkFlag);

    Integer userRegister(TbUser user);

    Map userLogin(String username, String password);

    TbUser getUserByToken(String token);

    Boolean logOut(String token);
}
