package io.beka.middleware;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String locale = request.getHeader("Accept-Language");
        System.out.println("Middleware Authorization : preHandle,  LocaleContextHolder.getLocale() : "+ LocaleContextHolder.getLocale()+", Accept-Language : "+locale);
//        if(true){
//            throw new NoAuthorizationException();
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("Middleware Authorization : postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("Middleware Authorization : afterCompletion");
    }
}
