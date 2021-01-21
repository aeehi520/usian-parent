package com.usian.feign;

import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName : SearchItemFeign
 * @Author : lenovo
 * @Date: 2021/1/18 19:00
 */
@FeignClient("usian-search-service")
public interface SearchItemFeign {
    @RequestMapping("/service/searchItem/importAll")
    boolean importAll();
    @RequestMapping("/service/searchItem/list")
    List<SearchItem> selectByQ(@RequestParam String q,@RequestParam Long page,@RequestParam Integer pageSize);
}
