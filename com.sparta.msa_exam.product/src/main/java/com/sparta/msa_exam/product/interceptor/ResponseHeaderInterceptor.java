package com.sparta.msa_exam.product.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

//@Slf4j(topic = "ResponseHeaderInterceptor")
//public class ResponseHeaderInterceptor implements HandlerInterceptor {
//
//    private String serverPort;
//
//    public ResponseHeaderInterceptor(String serverPort) {
//        this.serverPort = serverPort;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String forwardedPort = request.getHeader("X-Forwarded-Port");
//
//        log.info("forwardedPort Check : " + forwardedPort);
//        log.info("serverPort Check : " + serverPort);
//
//        // 요청 처리 후 헤더 추가
//        //response.addHeader("Server-Port", serverPort);
//        response.setHeader("Server-Port", serverPort);
//        log.info("Server-Port header added in postHandle: " + serverPort);
//    }
//}
