package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.TokenRefreshResult;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public TokenRefreshResult refresh(
            String refreshToken
    ) {

        if (refreshToken == null
                || refreshToken.isBlank()
                || !jwtTokenProvider
                .validateRefreshToken(refreshToken)) {

            throw new BadCredentialsException(
                    "유효하지 않은 Refresh Token입니다."
            );
        }

        UUID userId =
                jwtTokenProvider
                        .getUserId(refreshToken);

        UserResponse userResponse =
                userService.read(userId);

        String newAccessToken =
                jwtTokenProvider
                        .generateAccessToken(
                                userResponse.getId(),
                                userResponse.getUsername(),
                                userResponse.getRole()
                        );

        /*
         * Rotation
         *
         * 기존 Refresh Token을 그대로 반환하지 않고
         * 새로운 Refresh Token을 생성한다.
         */
        String newRefreshToken =
                jwtTokenProvider
                        .generateRefreshToken(
                                userResponse.getId(),
                                userResponse.getUsername(),
                                userResponse.getRole()
                        );

        JwtDto jwtDto =
                new JwtDto(
                        userResponse,
                        newAccessToken
                );

        return new TokenRefreshResult(
                jwtDto,
                newRefreshToken
        );
    }
}