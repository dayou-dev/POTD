package com.jhw.potd.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jhw.potd.global.EncryptPasswordEncoder;
import com.jhw.potd.controller.dto.request.SignUpRequest;
import com.jhw.potd.domain.User;
import com.jhw.potd.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

/**
 * 1. @ExtendWith(MockitoExtension.class)
 * JUnit5 테스트 클래스에서 Mockito 프레임워크를 사용하도록 연동하는 설정 어노테이션
 * 해당 설정이 있어야 @Mock, @InjectMocks 사용 가능
 * 2. @InjectMocks
 * 테스트 대상이되는 실제 객체에 사용
 * @Mock 생성한 가짜 객체들을 이 테스트 대상 객체에 자동으로 의존성 주입(DI)
 * 3. @Mock
 * 실제 동작이 되지 않는 가짜 객체 생, 테스트 대상이 의존하는 외부 객체에 사용(API client, Repository)
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;
	@Mock
	private HttpSession httpSession;
	@Mock
	EncryptPasswordEncoder encoder;
	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName(value = "회원가입 성공")
	void signUp_success() {
		SignUpRequest signUpRequest = new SignUpRequest("test@email.com", "pwd123", "test");
		userService.signUp(signUpRequest);
		verify(encoder, times(1)).encode(signUpRequest.getPassword());
		verify(userRepository, times(1)).save(any(User.class));
	}
}
