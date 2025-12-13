package dev.stocky.api.global.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;


  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    // 1. 헤더에서 토큰 추출
    String token = resolveToken(request);

    // 2. 토큰이 있고 유효하다면
    if (token != null && jwtTokenProvider.validateToken(token)) {
      // 3. 토큰 뜯어서 정보 가져오기
      Claims claims = jwtTokenProvider.parseClaims(token);

      String email = claims.getSubject(); // 이메일
      String role = (String) claims.get("role"); // 토큰에 저장된 Role 꺼내기

      // 4. 꺼낸 Role로 권한 객체 생성
      SimpleGrantedAuthority authority;
      if (role == null) {
        log.warn("토큰에 Role 정보가 없습니다. 기본 권한(ROLE_USER)으로 설정합니다. email: {}", email); // 로그 추가
        authority = new SimpleGrantedAuthority("ROLE_USER");
      } else {
        authority = new SimpleGrantedAuthority(role);
      }

      // 5. Authentication 객체 생성
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          email, // Principal (주체): username = email
          "", // Credentials (비밀번호): 비밀번호는 이미 토큰으로 증명
          Collections.singletonList(authority) // Authorities (권한): role 기반 권한 설정
      );

      // 4. SecurityContext에 authentication 저장 (이게 되어야 스프링 시큐리티가 "로그인 됐다"고 인식함)
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 5. 다음 필터로 진행
    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
