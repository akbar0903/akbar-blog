package com.akbar.config;

import com.akbar.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器，
 * 通过 addInterceptors 方法将该拦截器添加到 Spring 的拦截器链中。
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    //把登录拦截器注入
    private final LoginInterceptor loginInterceptor;

    @Autowired
    public WebConfiguration(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    /**
     * 
     * @param registry
     * 默认是拦截所有的接口，可以通过excludePathPatterns方法排除不需要拦截的接口
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/admin/login");
    }
}
