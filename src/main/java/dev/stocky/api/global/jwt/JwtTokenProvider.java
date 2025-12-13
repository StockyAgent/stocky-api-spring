package dev.stocky.api.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final Key key;
  private final long accessTokenValidityInMilliseconds;

  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenValidityInMilliseconds;

  // application.yml에서 설정한 값을 가져오기
  public JwtTokenProvider(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-token-expiration}") long accessTokenValidityInMilliseconds
  ) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
  }

  // Access Token 생성
  public String createAccessToken(String email, String role) {
    Claims claims = Jwts.claims().setSubject(email); // 토큰 제목에 이메일 설정
    claims.put("role", role); // 추가 클레임에 역할 설정

    Date now = new Date();
    Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity) // 만료 시간
        .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256) // 암호화 알고리즘
        .compact();
  }

  // Refresh Token 생성
  public String createRefreshToken(String email) {
    Claims claims = Jwts.claims().setSubject(email);

    Date now = new Date();
    Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰에서 이메일 추출
  public String getEmailFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
    }

    return false;
  }

  // 토큰에서 모든 정보(Claims) 추출
  public Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }


}
