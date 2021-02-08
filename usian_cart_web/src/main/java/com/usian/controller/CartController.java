package com.usian.controller;

import com.usian.feign.CartFeign;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/frontend/cart")
public class CartController {

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    private Integer CART_COOKIE_EXPIRE;

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @Autowired
    private CartFeign cartServiceFeign;

    @RequestMapping("/addItem")
    public Result addItem(String itemId, String userId,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(defaultValue = "1") Integer num){
        try {
            if(StringUtils.isBlank(userId)){
                //从cookie中取出购物车
                Map<String, TbItem> cart = getCartFromCookie(request);

                //把商品添加到购物车
                addItemToCart(itemId,cart,num);

                //把购物车添加到cookie
                addCartToCookie(request,response,cart);
            }else{
                //已登录
                //从redis中取出购物车
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                //把商品添加到购物车
                addItemToCart(itemId,cart,num);

                //把购物车放到redis
                cartServiceFeign.addCartToRedis(userId,cart);
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("添加失败");
    }

    @RequestMapping("/showCart")
    public Result showCart(String userId,HttpServletRequest request){
        List<TbItem> tbItemList = new ArrayList<>();
        if(StringUtils.isBlank(userId)){
            //从cookie中获得购物车
            Map<String, TbItem> cart = getCartFromCookie(request);
            if(cart==null || cart.size()==0){
                return Result.error("查询失败");
            }
            Set<String> keySet = cart.keySet();
            for(String key: keySet){
                TbItem tbItem = cart.get(key);
                tbItemList.add(tbItem);
            }
        }else{
            //已登录
            //从redis中获得购物车
            Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
            if(cart==null || cart.size()==0){
                return Result.error("查询失败");
            }
            Set<String> keySet = cart.keySet();
            for(String key: keySet){
                TbItem tbItem = cart.get(key);
                tbItemList.add(tbItem);
            }
        }
        return Result.ok(tbItemList);
    }

    @RequestMapping("/updateItemNum")
    public Result updateItemNum(String userId, String itemId,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                Integer num){
        try {
            if(StringUtils.isBlank(userId)){
                //从cookie中取出购物车
                Map<String, TbItem> cart = getCartFromCookie(request);

                //修改购物车
                TbItem tbItem = cart.get(itemId);
                tbItem.setNum(num);
                cart.put(itemId,tbItem);

                //把购物车添加到cookie
                addCartToCookie(request,response,cart);
            }else{
                //已登录
                //从redis中取出购物车
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                //修改购物车
                TbItem tbItem = cart.get(itemId);
                tbItem.setNum(num);
                cart.put(itemId,tbItem);

                //把购物车添加到cookie
                cartServiceFeign.addCartToRedis(userId,cart);
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("修改失败");
    }

    @RequestMapping("/deleteItemFromCart")
    public Result deleteItemFromCart(String userId,
                                     String itemId,
                                     HttpServletRequest request,
                                     HttpServletResponse response){
        try {
            if(StringUtils.isBlank(userId)){
                //从cookie中取出购物车
                Map<String, TbItem> cart = getCartFromCookie(request);

                //删除购物车中的商品
                cart.remove(itemId);

                //把购物车添加到cookie
                addCartToCookie(request,response,cart);
            }else{
                //已登录
                //从redis中取出购物车
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                //删除购物车中的商品
                cart.remove(itemId);
                //把购物车添加到cookie
                cartServiceFeign.addCartToRedis(userId,cart);
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("删除失败");
    }

    private void addCartToCookie(HttpServletRequest request, HttpServletResponse response, Map<String, TbItem> cart) {
        CookieUtils.setCookie(request,response,CART_COOKIE_KEY,JsonUtils.objectToJson(cart),CART_COOKIE_EXPIRE,true);
    }

    /**
     * 把商品添加到购物车
     * @param itemId
     * @param cart
     */
    private void addItemToCart(String itemId, Map<String, TbItem> cart,Integer num) {
        TbItem tbItem = cart.get(itemId);
        if(tbItem!=null){
            tbItem.setNum(tbItem.getNum()+num);
        }else{
            tbItem = itemServiceFeign.selectItemInfo(Long.valueOf(itemId));
            tbItem.setNum(num);
        }
        cart.put(itemId,tbItem);
    }

    /**
     * 从cookie查询购物车
     * @return
     * @param request
     */
    private Map<String, TbItem> getCartFromCookie(HttpServletRequest request) {
        String cookieJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);
        if(StringUtils.isNotBlank(cookieJson)){
            return JsonUtils.jsonToMap(cookieJson, TbItem.class);
        }
        return new HashMap<String,TbItem>();
    }
}
