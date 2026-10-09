package com.jhw.potd.global.interceptor;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {
	public static final String LOG_ID = "logId";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {

		String requestURI = request.getRequestURI();
		String uuid = UUID.randomUUID().toString();

		request.setAttribute(LOG_ID, uuid);
		// @RequestMapping : HandlerMethod
		// 정적 리소스 : ResourceRequestHandler
		if (handler instanceof HandlerMethod) {
			// 호출할 컨트롤러 메서드의 모든 정보 포함
			HandlerMethod hm = (HandlerMethod)handler;
		}

		log.info("REQUEST [{}],[{}],[{}]", uuid, requestURI, handler);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		@Nullable Exception ex) throws Exception {

		String requestURI = request.getRequestURI();
		String logId = (String)request.getAttribute(LOG_ID);

		log.info("RESPONSE [{}],[{}]", logId, requestURI);
		if (ex != null) {
			log.info("after completion error", ex);
		}
	}
}
