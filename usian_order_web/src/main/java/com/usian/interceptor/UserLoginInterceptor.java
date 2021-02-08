package com.usian.interceptor;

import com.usian.feign.SSOFeign;
import com.usian.pojo.TbUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName : UserLoginInterceptor
 * @Author : lenovo
 * @Date: 2021/1/30 11:05
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private SSOFeign ssoFeign;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //对用户的token做判断
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)){
            return false;
        }
        //如果用户token不为空，则校验用户在redis中是否失效
        TbUser user = ssoFeign.getUserByToken(token);
        if (user==null){
            return false;
        }
        return true;
    }
}
