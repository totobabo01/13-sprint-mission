package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("인증 및 Spring Security 통합 테스트")
class AuthSecurityIntegrationTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("CSRF 토큰")
    class CsrfTokenTest {

        @Test
        @DisplayName("CSRF 토큰 요청 시 203과 XSRF-TOKEN 쿠키를 반환한다")
        void should_ReturnCsrfTokenCookie_when_CsrfTokenIsRequested()
                throws Exception {

            // when
            MvcResult result =
                    mockMvc.perform(
                                    get("/api/auth/csrf-token")
                            )
                            .andExpect(
                                    status()
                                            .isNonAuthoritativeInformation()
                            )
                            .andReturn();

            // then
            Cookie csrfCookie =
                    result.getResponse()
                            .getCookie("XSRF-TOKEN");

            assertThat(csrfCookie)
                    .isNotNull();

            assertThat(csrfCookie.getValue())
                    .isNotBlank();

            assertThat(csrfCookie.isHttpOnly())
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("로그인")
    class LoginTest {

        @Test
        @DisplayName("올바른 사용자 이름과 비밀번호로 로그인하면 200과 JWT 정보를 반환한다")
        void should_ReturnJwtDto_when_CredentialsAreValid()
                throws Exception {

            // given
            createUser(
                    "securityUser",
                    "security-user@test.com",
                    "password"
            );

            CsrfData csrfData =
                    getCsrfData();

            // when & then
            MvcResult result =
                    mockMvc.perform(
                                    post("/api/auth/login")
                                            .cookie(
                                                    csrfData.cookie()
                                            )
                                            .header(
                                                    "X-XSRF-TOKEN",
                                                    csrfData.token()
                                            )
                                            .contentType(
                                                    MediaType.APPLICATION_FORM_URLENCODED
                                            )
                                            .param(
                                                    "username",
                                                    "securityUser"
                                            )
                                            .param(
                                                    "password",
                                                    "password"
                                            )
                            )
                            .andExpect(
                                    status().isOk()
                            )
                            .andExpect(
                                    jsonPath("$.userDto.username")
                                            .value("securityUser")
                            )
                            .andExpect(
                                    jsonPath("$.userDto.email")
                                            .value(
                                                    "security-user@test.com"
                                            )
                            )
                            .andExpect(
                                    jsonPath("$.userDto.id")
                                            .isNotEmpty()
                            )
                            .andExpect(
                                    jsonPath("$.userDto.role")
                                            .value("USER")
                            )
                            .andExpect(
                                    jsonPath("$.accessToken")
                                            .isNotEmpty()
                            )
                            .andExpect(
                                    cookie()
                                            .exists("REFRESH_TOKEN")
                            )
                            .andReturn();

            Cookie refreshTokenCookie =
                    result.getResponse()
                            .getCookie("REFRESH_TOKEN");

            assertThat(refreshTokenCookie)
                    .isNotNull();

            assertThat(refreshTokenCookie.getValue())
                    .isNotBlank();

            assertThat(refreshTokenCookie.isHttpOnly())
                    .isTrue();
        }

        @Test
        @DisplayName("비밀번호가 틀리면 401과 ErrorResponse를 반환한다")
        void should_ReturnUnauthorized_when_PasswordIsIncorrect()
                throws Exception {

            // given
            createUser(
                    "securityUser",
                    "security-user@test.com",
                    "password"
            );

            CsrfData csrfData =
                    getCsrfData();

            // when & then
            mockMvc.perform(
                            post("/api/auth/login")
                                    .cookie(
                                            csrfData.cookie()
                                    )
                                    .header(
                                            "X-XSRF-TOKEN",
                                            csrfData.token()
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_FORM_URLENCODED
                                    )
                                    .param(
                                            "username",
                                            "securityUser"
                                    )
                                    .param(
                                            "password",
                                            "wrongPassword"
                                    )
                    )
                    .andExpect(
                            status().isUnauthorized()
                    )
                    .andExpect(
                            jsonPath("$.status")
                                    .value(401)
                    )
                    .andExpect(
                            jsonPath("$.code")
                                    .value("AUTH-001")
                    )
                    .andExpect(
                            jsonPath("$.message")
                                    .value(
                                            "아이디 또는 비밀번호가 올바르지 않습니다."
                                    )
                    )
                    .andExpect(
                            jsonPath("$.exceptionType")
                                    .value(
                                            "BadCredentialsException"
                                    )
                    );
        }

        @Test
        @DisplayName("CSRF 토큰 없이 로그인하면 403 Forbidden을 반환한다")
        void should_ReturnForbidden_when_CsrfTokenIsMissing()
                throws Exception {

            // given
            createUser(
                    "securityUser",
                    "security-user@test.com",
                    "password"
            );

            // when & then
            mockMvc.perform(
                            post("/api/auth/login")
                                    .contentType(
                                            MediaType.APPLICATION_FORM_URLENCODED
                                    )
                                    .param(
                                            "username",
                                            "securityUser"
                                    )
                                    .param(
                                            "password",
                                            "password"
                                    )
                    )
                    .andExpect(
                            status().isForbidden()
                    );
        }

        @Test
        @DisplayName("동일한 계정으로 여러 번 로그인해도 세션을 생성하지 않고 각각 JWT를 발급한다")
        void should_IssueJwtWithoutSession_when_SameUserLogsInAgain()
                throws Exception {

            // given
            createUser(
                    "concurrentUser",
                    "concurrent-user@test.com",
                    "password"
            );

            // when
            LoginData firstLogin =
                    login(
                            "concurrentUser",
                            "password"
                    );

            LoginData secondLogin =
                    login(
                            "concurrentUser",
                            "password"
                    );

            // then
            assertThat(firstLogin.accessToken())
                    .isNotBlank();

            assertThat(secondLogin.accessToken())
                    .isNotBlank();

            assertThat(firstLogin.refreshToken())
                    .isNotBlank();

            assertThat(secondLogin.refreshToken())
                    .isNotBlank();
        }
    }

    @Nested
    @DisplayName("현재 사용자 조회")
    class CurrentUserTest {

        @Test
        @DisplayName("Access Token으로 현재 사용자 정보를 조회하면 200과 사용자 정보를 반환한다")
        void should_ReturnCurrentUser_when_AccessTokenIsValid()
                throws Exception {

            // given
            createUser(
                    "currentUser",
                    "current-user@test.com",
                    "password"
            );

            LoginData loginData =
                    login(
                            "currentUser",
                            "password"
                    );

            // when & then
            mockMvc.perform(
                            get("/api/auth/me")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + loginData.accessToken()
                                    )
                    )
                    .andExpect(
                            status().isOk()
                    )
                    .andExpect(
                            jsonPath("$.id")
                                    .isNotEmpty()
                    )
                    .andExpect(
                            jsonPath("$.username")
                                    .value("currentUser")
                    )
                    .andExpect(
                            jsonPath("$.email")
                                    .value(
                                            "current-user@test.com"
                                    )
                    )
                    .andExpect(
                            jsonPath("$.role")
                                    .value("USER")
                    );
        }

        @Test
        @DisplayName("Access Token 없이 현재 사용자 조회 시 401을 반환한다")
        void should_ReturnUnauthorized_when_AccessTokenIsMissing()
                throws Exception {

            // when & then
            mockMvc.perform(
                            get("/api/auth/me")
                    )
                    .andExpect(
                            status().isUnauthorized()
                    );
        }

        @Test
        @DisplayName("잘못된 Access Token으로 현재 사용자 조회 시 401을 반환한다")
        void should_ReturnUnauthorized_when_AccessTokenIsInvalid()
                throws Exception {

            // when & then
            mockMvc.perform(
                            get("/api/auth/me")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER + "invalid.jwt.token"
                                    )
                    )
                    .andExpect(
                            status().isUnauthorized()
                    );
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("JWT 인증 사용자가 로그아웃하면 204 No Content를 반환한다")
        void should_ReturnNoContent_when_LogoutSucceeds()
                throws Exception {

            // given
            createUser(
                    "logoutUser",
                    "logout-user@test.com",
                    "password"
            );

            LoginData loginData =
                    login(
                            "logoutUser",
                            "password"
                    );

            CsrfData csrfData =
                    getCsrfData();

            // when & then
            mockMvc.perform(
                            post("/api/auth/logout")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + loginData.accessToken()
                                    )
                                    .cookie(
                                            csrfData.cookie()
                                    )
                                    .header(
                                            "X-XSRF-TOKEN",
                                            csrfData.token()
                                    )
                    )
                    .andExpect(
                            status().isNoContent()
                    );
        }
    }

    @Nested
    @DisplayName("권한")
    class RoleTest {

        @Test
        @DisplayName("회원가입 시 기본 권한은 USER이다")
        void should_AssignUserRole_when_UserIsCreated()
                throws Exception {

            // given
            UserCreateRequest request =
                    new UserCreateRequest(
                            "roleUser",
                            "role-user@test.com",
                            "password",
                            null
                    );

            CsrfData csrfData =
                    getCsrfData();

            // when & then
            mockMvc.perform(
                            post("/api/users")
                                    .cookie(
                                            csrfData.cookie()
                                    )
                                    .header(
                                            "X-XSRF-TOKEN",
                                            csrfData.token()
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    request
                                            )
                                    )
                    )
                    .andExpect(
                            status().isOk()
                    )
                    .andExpect(
                            jsonPath("$.username")
                                    .value("roleUser")
                    )
                    .andExpect(
                            jsonPath("$.role")
                                    .value("USER")
                    );
        }

        @Test
        @DisplayName("ADMIN이 사용자 권한을 CHANNEL_MANAGER로 변경하면 200과 변경된 사용자 정보를 반환한다")
        void should_UpdateRoleToChannelManager_when_AdminRequestsRoleUpdate()
                throws Exception {

            // given
            String userId =
                    createUser(
                            "channelManagerUser",
                            "channel-manager@test.com",
                            "password"
                    );

            createAdmin(
                    "roleAdmin1",
                    "role-admin1@test.com",
                    "password"
            );

            LoginData adminLogin =
                    login(
                            "roleAdmin1",
                            "password"
                    );

            CsrfData csrfData =
                    getCsrfData();

            String requestBody = """
                    {
                        "userId": "%s",
                        "newRole": "CHANNEL_MANAGER"
                    }
                    """.formatted(userId);

            // when & then
            mockMvc.perform(
                            put("/api/auth/role")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + adminLogin.accessToken()
                                    )
                                    .cookie(
                                            csrfData.cookie()
                                    )
                                    .header(
                                            "X-XSRF-TOKEN",
                                            csrfData.token()
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(requestBody)
                    )
                    .andExpect(
                            status().isOk()
                    )
                    .andExpect(
                            jsonPath("$.id")
                                    .value(userId)
                    )
                    .andExpect(
                            jsonPath("$.username")
                                    .value(
                                            "channelManagerUser"
                                    )
                    )
                    .andExpect(
                            jsonPath("$.role")
                                    .value(
                                            "CHANNEL_MANAGER"
                                    )
                    );
        }

        @Test
        @DisplayName("ADMIN이 사용자 권한을 ADMIN으로 변경하면 200과 ADMIN 권한을 반환한다")
        void should_UpdateRoleToAdmin_when_AdminRequestsRoleUpdate()
                throws Exception {

            // given
            String userId =
                    createUser(
                            "adminRoleUser",
                            "admin-role-user@test.com",
                            "password"
                    );

            createAdmin(
                    "roleAdmin2",
                    "role-admin2@test.com",
                    "password"
            );

            LoginData adminLogin =
                    login(
                            "roleAdmin2",
                            "password"
                    );

            CsrfData csrfData =
                    getCsrfData();

            String requestBody = """
                    {
                        "userId": "%s",
                        "newRole": "ADMIN"
                    }
                    """.formatted(userId);

            // when & then
            mockMvc.perform(
                            put("/api/auth/role")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + adminLogin.accessToken()
                                    )
                                    .cookie(
                                            csrfData.cookie()
                                    )
                                    .header(
                                            "X-XSRF-TOKEN",
                                            csrfData.token()
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(requestBody)
                    )
                    .andExpect(
                            status().isOk()
                    )
                    .andExpect(
                            jsonPath("$.id")
                                    .value(userId)
                    )
                    .andExpect(
                            jsonPath("$.role")
                                    .value("ADMIN")
                    );
        }

        @Test
        @DisplayName("CSRF 토큰 없이 사용자 권한을 수정하면 403 Forbidden을 반환한다")
        void should_ReturnForbidden_when_RoleUpdateHasNoCsrfToken()
                throws Exception {

            // given
            String userId =
                    createUser(
                            "csrfRoleUser",
                            "csrf-role-user@test.com",
                            "password"
                    );

            createAdmin(
                    "csrfRoleAdmin",
                    "csrf-role-admin@test.com",
                    "password"
            );

            LoginData adminLogin =
                    login(
                            "csrfRoleAdmin",
                            "password"
                    );

            String requestBody = """
                    {
                        "userId": "%s",
                        "newRole": "CHANNEL_MANAGER"
                    }
                    """.formatted(userId);

            // when & then
            mockMvc.perform(
                            put("/api/auth/role")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + adminLogin.accessToken()
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(requestBody)
                    )
                    .andExpect(
                            status().isForbidden()
                    );
        }

        @Test
        @DisplayName("기존 Access Token으로 요청해도 변경된 최신 Role이 적용된다")
        void should_UseLatestRole_when_UserRoleIsUpdated()
                throws Exception {

            // given
            String targetUserId =
                    createUser(
                            "jwtRoleUser",
                            "jwt-role-user@test.com",
                            "password"
                    );

            LoginData targetLogin =
                    login(
                            "jwtRoleUser",
                            "password"
                    );

            createAdmin(
                    "jwtRoleAdmin",
                    "jwt-role-admin@test.com",
                    "password"
            );

            LoginData adminLogin =
                    login(
                            "jwtRoleAdmin",
                            "password"
                    );

            CsrfData csrfData =
                    getCsrfData();

            String requestBody = """
                    {
                        "userId": "%s",
                        "newRole": "CHANNEL_MANAGER"
                    }
                    """.formatted(targetUserId);

            mockMvc.perform(
                            put("/api/auth/role")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + adminLogin.accessToken()
                                    )
                                    .cookie(
                                            csrfData.cookie()
                                    )
                                    .header(
                                            "X-XSRF-TOKEN",
                                            csrfData.token()
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(requestBody)
                    )
                    .andExpect(
                            status().isOk()
                    )
                    .andExpect(
                            jsonPath("$.role")
                                    .value("CHANNEL_MANAGER")
                    );

            // when & then
            mockMvc.perform(
                            get("/api/auth/me")
                                    .header(
                                            AUTHORIZATION,
                                            BEARER
                                                    + targetLogin.accessToken()
                                    )
                    )
                    .andExpect(
                            status().isOk()
                    )
                    .andExpect(
                            jsonPath("$.role")
                                    .value("CHANNEL_MANAGER")
                    );
        }
    }

    private String createUser(
            String username,
            String email,
            String password
    ) throws Exception {

        UserCreateRequest request =
                new UserCreateRequest(
                        username,
                        email,
                        password,
                        null
                );

        CsrfData csrfData =
                getCsrfData();

        MvcResult result =
                mockMvc.perform(
                                post("/api/users")
                                        .cookie(
                                                csrfData.cookie()
                                        )
                                        .header(
                                                "X-XSRF-TOKEN",
                                                csrfData.token()
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        request
                                                )
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andReturn();

        JsonNode body =
                objectMapper.readTree(
                        result.getResponse()
                                .getContentAsString()
                );

        return body.get("id")
                .asText();
    }

    private void createAdmin(
            String username,
            String email,
            String password
    ) throws Exception {

        String userId =
                createUser(
                        username,
                        email,
                        password
                );

        User user =
                userRepository.findById(
                                UUID.fromString(
                                        userId
                                )
                        )
                        .orElseThrow();

        user.updateRole(
                Role.ADMIN
        );

        userRepository.saveAndFlush(
                user
        );
    }

    private LoginData login(
            String username,
            String password
    ) throws Exception {

        CsrfData csrfData =
                getCsrfData();

        MvcResult loginResult =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .cookie(
                                                csrfData.cookie()
                                        )
                                        .header(
                                                "X-XSRF-TOKEN",
                                                csrfData.token()
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_FORM_URLENCODED
                                        )
                                        .param(
                                                "username",
                                                username
                                        )
                                        .param(
                                                "password",
                                                password
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andExpect(
                                jsonPath("$.accessToken")
                                        .isNotEmpty()
                        )
                        .andReturn();

        JsonNode body =
                objectMapper.readTree(
                        loginResult.getResponse()
                                .getContentAsString()
                );

        String accessToken =
                body.get("accessToken")
                        .asText();

        Cookie refreshTokenCookie =
                loginResult.getResponse()
                        .getCookie("REFRESH_TOKEN");

        assertThat(refreshTokenCookie)
                .isNotNull();

        assertThat(refreshTokenCookie.getValue())
                .isNotBlank();

        return new LoginData(
                accessToken,
                refreshTokenCookie.getValue()
        );
    }

    private CsrfData getCsrfData()
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                get("/api/auth/csrf-token")
                        )
                        .andExpect(
                                status()
                                        .isNonAuthoritativeInformation()
                        )
                        .andReturn();

        Cookie csrfCookie =
                result.getResponse()
                        .getCookie("XSRF-TOKEN");

        assertThat(csrfCookie)
                .isNotNull();

        assertThat(csrfCookie.getValue())
                .isNotBlank();

        return new CsrfData(
                csrfCookie,
                csrfCookie.getValue()
        );
    }

    private record LoginData(
            String accessToken,
            String refreshToken
    ) {
    }

    private record CsrfData(
            Cookie cookie,
            String token
    ) {
    }
}