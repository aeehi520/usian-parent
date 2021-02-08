package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName : SSoServiceImpl
 * @Author : lenovo
 * @Date: 2021/1/27 10:01
 */
@Service
@Transactional
public class SSoServiceImpl implements SSOService{
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private RedisClient redisClient;
    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;
    @Value("${USER_INFO}")
    private String USER_INFO;

    /**
     * 对用户的注册信息做数据校验
     * @param checkValue
     * @param checkFlag
     * @return
     */
    @Override
    public Boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        if (checkFlag==1){
            criteria.andUsernameEqualTo(checkValue);
        }else {
            criteria.andPhoneEqualTo(checkValue);
        }
        List<TbUser> tbUsers = userMapper.selectByExample(tbUserExample);
        if (tbUsers==null||tbUsers.size()<=0){
            return true;
        }
        return false;
    }

    @Override
    public Integer userRegister(TbUser user) {
        String pwd = MD5Utils.digest(user.getPassword());
        user.setPassword(pwd);
        user.setCreated(new Date());
        user.setUpdated(new Date());
        int num = userMapper.insertSelective(user);
        return num;
    }

    @Override
    public Map userLogin(String username, String password) {
        String pwd = MD5Utils.digest(password);
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(pwd);
        List<TbUser> tbUsers = userMapper.selectByExample(tbUserExample);
        //登陆失败
        if (tbUsers==null||tbUsers.size()<=0){
            return null;
        }
        //登录成功
        TbUser tbUser = tbUsers.get(0);
        tbUser.setPassword(null);
        String token = UUID.randomUUID().toString();
        redisClient.set(USER_INFO+":"+token,tbUser);
        redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);
        Map<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("userid",tbUser.getId().toString());
        map.put("username",tbUser.getUsername());
        return map;
    }

    @Override
    public TbUser getUserByToken(String token) {
        TbUser tbUser = (TbUser) redisClient.get(USER_INFO + ":" + token);
        if (tbUser!=null){
            redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);
            return tbUser;
        }
        return null;
    }

    @Override
    public Boolean logOut(String token) {
        return redisClient.del(USER_INFO+":"+token);
    }
}
