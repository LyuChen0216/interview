package com.lyu.broken.pojo.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private T data;

    private boolean status;

    private String url;

    public Result(T data, Boolean status) {
        this.data = data;
        this.status = status;
    }

    public Result(String url,Boolean status) {
        this.url = url;
        this.status = status;
    }
}
