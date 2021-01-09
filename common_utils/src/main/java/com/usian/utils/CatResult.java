package com.usian.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName : CatResult
 * @Author : lenovo
 * @Date: 2021/1/8 17:18
 */
public class CatResult implements Serializable {
    private List<?> data;

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
