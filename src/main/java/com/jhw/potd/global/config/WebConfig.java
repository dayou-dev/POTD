package com.jhw.potd.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jhw.potd.global.interceptor.LogInterceptor;
import com.jhw.potd.global.interceptor.LoginCheckInterceptor;
import com.jhw.potd.global.LoginUserResolver;
import com.jhw.potd.global.interceptor.QueryCounterInterceptor;
import com.jhw.potd.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final UserService userService;
	private final LogInterceptor logInterceptor;
	private final QueryCounterInterceptor queryCounterInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logInterceptor).order(1);
		registry.addInterceptor(queryCounterInterceptor).order(2);
		registry.addInterceptor(new LoginCheckInterceptor())
			.order(3)
			.addPathPatterns("/v1/**") // 보호할 경로
			.excludePathPatterns("/users/v1/login", "/users/v1/sign-up", "/feeds/v1/list");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginUserResolver(userService));
	}
}
