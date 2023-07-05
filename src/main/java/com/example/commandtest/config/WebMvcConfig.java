package com.example.commandtest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Request가 슬랙에서 보낸 유효한 Request인지 확인하는 기능
 */

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HandlerInterceptor handlerInterceptor;

    public WebMvcConfig(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor);
    }
}