package com.akbar.utils;

import java.util.Map;

/**
 * ThreadLocal 工具类
 */
@SuppressWarnings("all")
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL_CLAIMS = new ThreadLocal<>();
    private static final ThreadLocal<String> THREAD_LOCAL_IP = new ThreadLocal<>();

    //根据键获取值
    public static Map<String, Object> getClaims(){
        return THREAD_LOCAL_CLAIMS.get();
    }

    //获取IPi地址
    public static String getIP() {
        return THREAD_LOCAL_IP.get();
    }
	
    //存储键值对
    public static void setClaims(Map<String, Object> value){
        THREAD_LOCAL_CLAIMS.set(value);
    }

    //存储IP地址
    public static void setIP(String value){
        THREAD_LOCAL_IP.set(value);
    }


    //清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL_CLAIMS.remove();
        THREAD_LOCAL_IP.remove();
    }
}
