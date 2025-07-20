package com.loopers.interceptor;

import com.loopers.interfaces.api.user.RequireUserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class HeaderValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            boolean required = handlerMethod.hasMethodAnnotation(RequireUserId.class);
            if (!required) {
                return true;
            }
            String userId = request.getHeader("X-USER-ID");

            if (userId == null || userId.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"meta\": {\"result\": \"FAIL\",\"errorCode\": \"Bad Request\",\"message\": \"중복된 사용자 ID 입니다.\"}}");
                response.getWriter().flush();
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
