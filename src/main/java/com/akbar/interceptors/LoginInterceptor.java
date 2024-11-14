package com.akbar.interceptors;

import com.akbar.utils.JwtUtil;
import com.akbar.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 请求拦截器
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 注入redis依赖
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 令牌验证
        String token = request.getHeader("Authorization");
        try {
            //从redis中获取令牌
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if (token == null || !token.equals(redisToken)) {
                throw new RuntimeException("Token验证失败");
            }

            Map<String, Object> claims = JwtUtil.parseToken(token);
            //把业务数据存到ThreadLocal中
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        } catch (Exception e) {
            response.setStatus(401);    //未授权
            return false;
        }
    }

    //清除ThreadLocal中的数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
