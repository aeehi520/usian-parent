package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName : SSOFeign
 * @Author : lenovo
 * @Date: 2021/1/27 9:55
 */
@FeignClient("usian-sso-service")
public interface SSOFeign {
    @RequestMapping("/service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    Boolean checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag);
    @RequestMapping("/service/sso/userRegister")
    Integer userRegister(@RequestBody TbUser user);
    @RequestMapping("/service/sso/userLogin")
    Map userLogin(@RequestParam String username,@RequestParam String password);
    @RequestMapping("/service/sso/getUserByToken/{token}")
    TbUser getUserByToken(@PathVariable String token);
    @RequestMapping("/service/sso/logOut")
    Boolean logOut(@RequestParam String token);
}
