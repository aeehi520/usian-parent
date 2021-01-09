package com.usian.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName : CatNode
 * @Author : lenovo
 * @Date: 2021/1/8 17:17
 */
public class CatNode implements Serializable {
    @JsonProperty("n")
    private String name;
    @JsonProperty("i")
    private List<?> item;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<?> getItem() {
        return item;
    }

    public void setItem(List<?> item) {
        this.item = item;
    }
}
