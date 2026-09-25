package com.jhw.potd.global;

import static com.jhw.potd.global.SessionConstant.*;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		HttpSession session = request.getSession();
		if (session == null || session.getAttribute(USER_ID) == null) {
			throw new CustomException(ErrorCode.SC_UNAUTHORIZED);
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
