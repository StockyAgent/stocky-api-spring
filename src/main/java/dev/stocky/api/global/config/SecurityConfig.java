package dev.stocky.api.global.config;

import dev.stocky.api.global.jwt.JwtAuthenticationFilter;
import dev.stocky.api.global.jwt.JwtTokenProvider;
import dev.stocky.api.global.oauth2.CustomOAuth2UserService;
import dev.stocky.api.global.oauth2.OAuth2LoginSuccessHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/error")
                .permitAll()
                .requestMatchers("/login/**", "/oauth2/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll() // ✅ Swagger 관련 경로 허용
                .requestMatchers("/api/batch/**").permitAll() // 배치 작업용 엔드포인트 허용
//            .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name()) // 관리자 권한이 필요한 경로 예시
                .anyRequest().authenticated()
        )

        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler)
        )

        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // 접근을 허용할 도메인 목록 todo: 추후 배포 시 yml로 주소 관리
    config.setAllowedOrigins(List.of(
        "http://localhost:3000", // 로컬 테스트용 (React/Next.js 기본 포트)
        "https://stocky.vercel.app", // (예시) 나중에 배포될 Vercel 주소
        "https://dev.stocky.vercel.app" // (예시) 개발용 Vercel 주소
    ));

    // 허용할 HTTP 메서드
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

    // 허용할 헤더
    config.setAllowedHeaders(List.of("*"));

    // 중요: 쿠키나 인증 헤더를 포함한 요청을 허용하려면 true여야 함
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 적용
    return source;
  }
}
