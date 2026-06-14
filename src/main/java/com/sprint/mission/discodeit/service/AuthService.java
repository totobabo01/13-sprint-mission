package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

// 인증 관련 기능을 정의하는 Service 인터페이스
// 로그인 같은 인증 기능의 규칙을 선언하는 역할
public interface AuthService {

    // 로그인 기능
    // username과 password가 일치하는 사용자가 있으면 UserResponse 반환
    // 일치하는 사용자가 없으면 구현체에서 예외 발생
    UserResponse login(LoginRequest request);
}