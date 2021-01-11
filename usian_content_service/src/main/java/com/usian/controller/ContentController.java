package com.usian.controller;

import com.usian.service.ContentService;
import com.usian.utils.AdNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName : ContentController
 * @Author : lenovo
 * @Date: 2021/1/11 18:42
 */
@RestController
@RequestMapping("/service/content")
public class ContentController {
    @Autowired
    private ContentService contentService;
    @RequestMapping("/selectFrontendContentByAD")
    public List<AdNode> selectFrontendContentByAD(){
        return contentService.selectFrontendContentByAD();
    }
}
