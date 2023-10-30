package com.bekaku.api.spring.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String locale = request.getHeader("Accept-Language");
//        logger.info("Middleware Authorization : preHandle,  LocaleContextHolder.getLocale() : "+ LocaleContextHolder.getLocale()+", Accept-Language : "+locale);
//        if(true){
//            throw new NoAuthorizationException();
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        logger.info("Middleware Authorization : postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        logger.info("Middleware Authorization : afterCompletion");
    }
}
