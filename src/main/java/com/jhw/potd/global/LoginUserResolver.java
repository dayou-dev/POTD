package com.jhw.potd.global;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.jhw.potd.service.UserService;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginUserResolver implements HandlerMethodArgumentResolver {
	private final UserService userService;
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// 파라미터에 @LoginUser가 붙어 있고, 타입이 UserSessionDto(또는 원하는 객체)인지 확인
		boolean hasAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
		boolean typeMatch = parameter.getParameterType().equals(Long.class);
		return hasAnnotation && typeMatch;
	}

	@Override
	public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
		return userService.getLoginUser();
	}
}
