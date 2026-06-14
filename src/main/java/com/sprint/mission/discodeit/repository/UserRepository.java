package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

// User 데이터를 저장하고 조회하기 위한 Repository 인터페이스
// Service는 이 인터페이스를 통해 User 저장소에 접근함
public interface UserRepository {

    // 사용자 저장
    // 새 사용자 생성 또는 기존 사용자 수정 후 저장할 때 사용
    User save(User user);

    // id로 사용자 한 명 조회
    User findById(UUID id);

    // 모든 사용자 조회
    List<User> findAll();

    // id로 사용자 삭제
    void deleteById(UUID id);

    // 해당 id를 가진 사용자가 존재하는지 확인
    boolean existsById(UUID id);

    // 같은 username을 가진 사용자가 이미 존재하는지 확인
    // 회원가입 또는 사용자 생성 시 중복 검사용
    boolean existsByUsername(String username);

    // 같은 email을 가진 사용자가 이미 존재하는지 확인
    // 회원가입 또는 사용자 생성 시 중복 검사용
    boolean existsByEmail(String email);

    // 추가한 부분: username으로 사용자 조회
    // AuthService에서 로그인할 때 사용
    User findByUsername(String username);
}