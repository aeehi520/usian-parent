package com.usian.controller;

import com.usian.feign.SSOFeign;
import com.usian.pojo.TbUser;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName : SSOController
 * @Author : lenovo
 * @Date: 2021/1/27 9:54
 */
@RestController
@RequestMapping("/frontend/sso")
public class SSOController {
    @Autowired
    private SSOFeign ssoFeign;
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(
            @PathVariable String checkValue,
            @PathVariable Integer checkFlag
    ){
        Boolean b = ssoFeign.checkUserInfo(checkValue,checkFlag);
        if (b){
            return Result.ok();
        }
        return Result.error("error");
    }
    @RequestMapping("/userRegister")
    public Result userRegister(TbUser user){
        Integer num = ssoFeign.userRegister(user);
        if (num==1){
            return Result.ok();
        }
        return Result.error("注册失败");
    }
    @RequestMapping("/userLogin")
    public Result userLogin(String username,String password){
        Map map = ssoFeign.userLogin(username,password);
        if (map!=null){
            return Result.ok(map);
        }
        return Result.error("登陆失败");
    }
    @RequestMapping("/getUserByToken/{token}")
    public Result getUserByToken(@PathVariable String token){
        TbUser tbUser = ssoFeign.getUserByToken(token);
        if (tbUser!=null){
            return Result.ok();
        }
        return Result.error("登录过期");
    }
    @RequestMapping("/logOut")
    public Result logOut(String token){
        Boolean b = ssoFeign.logOut(token);
        if (b) {
            return Result.ok();
        }
        return Result.error("退出失败");
    }
}
