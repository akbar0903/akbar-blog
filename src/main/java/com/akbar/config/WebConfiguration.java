package com.akbar.config;

import com.akbar.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    //把登录拦截器注入
    private final LoginInterceptor loginInterceptor;

    @Autowired
    public WebConfiguration(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    /**
     * 注册登录拦截器
     * @param registry
     * 默认是拦截所有的接口，可以通过excludePathPatterns方法排除不需要拦截的接口
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/admin/login");
    }
}
