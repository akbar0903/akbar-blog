package com.akbar.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // 如果没有有效的IP地址，通过代理服务器获取客户端的IP地址
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        //继续检查 WL-Proxy-Client-IP 请求头。这个头部通常由一些代理服务器（如 WebLogic 或其他应用服务器）设置，用于存储客户端的 IP 地址。
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // 获取到的是是客户端的直接连接 IP 地址。注意，如果有代理，可能会返回代理服务器的 IP 地址。
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
