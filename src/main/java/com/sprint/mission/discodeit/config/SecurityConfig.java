package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    private final CustomAuthenticationEntryPoint
            customAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler
            customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DiscodeitUserDetailsService discodeitUserDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository
                                        .withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // 정적 리소스
                        // 로그인 여부와 관계없이 프론트 화면 및 리소스 접근 허용
                        .requestMatchers(
                                "/",
                                "/*.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        // CSRF 토큰 발급
                        .requestMatchers(
                                "/api/auth/csrf-token"
                        ).permitAll()

                        // 로그인
                        .requestMatchers(
                                "/api/auth/login"
                        ).permitAll()

                        // 로그아웃
                        .requestMatchers(
                                "/api/auth/logout"
                        ).permitAll()

                        // 회원가입
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Actuator
                        .requestMatchers(
                                "/actuator/**"
                        ).permitAll()

                        // 그 외 모든 요청 인증 필요
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                customAuthenticationEntryPoint
                        )
                        .accessDeniedHandler(
                                customAccessDeniedHandler
                        )
                )

                /*
                 * JWT 기반 인증을 사용하므로
                 * 서버에서 인증 세션을 생성하거나 유지하지 않는다.
                 *
                 * 요청마다 Access Token을 통해 인증한다.
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * 기존 Sprint 9의 Remember-Me 설정.
                 *
                 * 현재 요구사항에서 제거하라는 내용이 없으므로
                 * 일단 유지한다.
                 *
                 * 이후 Refresh Token 요구사항에 따라
                 * 제거 여부를 결정할 수 있다.
                 */
                .rememberMe(remember -> remember
                        .key(
                                "discodeit-remember-me-key"
                        )
                        .rememberMeParameter(
                                "remember-me"
                        )
                        .rememberMeCookieName(
                                "remember-me"
                        )
                        .tokenValiditySeconds(
                                60 * 60 * 24 * 14
                        )
                        .userDetailsService(
                                discodeitUserDetailsService
                        )
                )

                /*
                 * 기존 formLogin 인증 방식은 유지한다.
                 *
                 * 로그인 성공 시 JwtLoginSuccessHandler에서
                 * Access Token과 Refresh Token을 발급한다.
                 */
                .formLogin(login -> login
                        .loginProcessingUrl(
                                "/api/auth/login"
                        )
                        .successHandler(
                                jwtLoginSuccessHandler
                        )
                        .failureHandler(
                                loginFailureHandler
                        )
                )

                .logout(logout -> logout
                        .logoutUrl(
                                "/api/auth/logout"
                        )
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(
                                        HttpStatus.NO_CONTENT
                                )
                        )
                )

                /*
                 * Authorization: Bearer {AccessToken}
                 * 요청을 처리하는 JWT 인증 필터.
                 *
                 * UsernamePasswordAuthenticationFilter보다 먼저 실행해서
                 * SecurityContext에 인증 정보를 등록한다.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /*
     * 기존 코드에서 SessionRegistry를 사용하는 부분이
     * 남아 있을 수 있으므로 우선 Bean은 유지한다.
     */
    @Bean
    public SessionRegistry sessionRegistry() {

        return new SessionRegistryImpl();
    }

    /*
     * 기존 세션 관련 코드와의 호환성을 위해 우선 유지한다.
     */
    @Bean
    public HttpSessionEventPublisher
    httpSessionEventPublisher() {

        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {

        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_CHANNEL_MANAGER
                ROLE_CHANNEL_MANAGER > ROLE_USER
                """);
    }

    @Bean
    static MethodSecurityExpressionHandler
    methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy
    ) {

        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(
                roleHierarchy
        );

        return handler;
    }
}