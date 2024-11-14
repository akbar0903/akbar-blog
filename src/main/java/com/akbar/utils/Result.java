package com.akbar.utils;

/**
 * 统一响应结果
 * @param <T>
 */
public class Result<T> {
    private Integer code;       //业务状态码  0-成功  1-失败
    private String message;     //提示信息
    private T data;             //响应数据,类型是object，可以对应其他类型

    /*-----------------------构造方法-----------------------*/
    // 无参构造函数
    public Result() {
    }
    // 全参构造函数
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * getter and setter
     * @return
     */
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 响应数据用的静态方法
     * @param data
     * @return
     * @param <E>
     */
    //快速返回操作成功响应结果(带响应数据)
    public static <E> Result<E> success(E data) {
        return new Result<>(0, "操作成功", data);
    }
    //快速返回操作成功响应结果
    public static Result success() {
        return new Result(0, "操作成功", null);
    }
    //失败之后响应的方法
    public static Result error(String message) {
        return new Result(1, message, null);
    }
}
