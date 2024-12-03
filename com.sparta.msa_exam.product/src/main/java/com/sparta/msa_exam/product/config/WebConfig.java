package com.sparta.msa_exam.product.config;

import com.sparta.msa_exam.product.interceptor.ResponseHeaderInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String serverPort;

    public WebConfig(@Value("${server.port}") String serverPort) {
        this.serverPort = serverPort;
    }

    @Bean
    public ResponseHeaderInterceptor ResponseHeaderInterceptor() {
        return new ResponseHeaderInterceptor(serverPort);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ResponseHeaderInterceptor())
                .addPathPatterns("/products/**");
    }
}
