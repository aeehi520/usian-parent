package com.usian.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : RedisClient
 * @Author : lenovo
 * @Date: 2021/1/11 20:53
 */
@Component
public class RedisClient {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 分布式锁
     * @param key
     * @param value
     * @return
     */
    public Boolean setnx(String key, Object value, long time) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, time,
                    TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean expire(String key,long time){
        try {
            if (time>0){
                redisTemplate.expire(key,time, TimeUnit.SECONDS);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public long ttl(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }
    public Boolean exists(String key){
        return redisTemplate.hasKey(key);
    }
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }
    public boolean set(String key,Object value){
        try {
            redisTemplate.opsForValue().set(key,value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public Boolean del(String key){
        return redisTemplate.delete(key);
    }
    public long incr(String key,long delta){
        if (delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key,delta);
    }
    public long decr(String key,long delta){
        if (delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key,-delta);
    }
    public Object hget(String key, String item){
        return redisTemplate.opsForHash().get(key, item);
    }
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }
    public Set<Object> smembers(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public long sadd(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public long srem(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public List<Object> lrange(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean rpush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean lpush(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public long lrem(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
