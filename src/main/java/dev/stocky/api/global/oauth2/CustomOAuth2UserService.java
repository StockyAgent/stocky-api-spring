package dev.stocky.api.global.oauth2;

import dev.stocky.api.domain.user.Role;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // 1. 기본 OAuth2UserService를 통해 Google에서 사용자 정보 가져오기
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    // 2. 어떤 서비스인지 확인 (google, naver 등) - 현재는 google만 사용
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    // 3. OAuth2 로그인 진행 시 키가 되는 필드값 (PK) - 구글은 "sub"
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    // 4. Google에서 받은 속성값들 (Map 형태)
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // 5. 우리 서비스의 User 엔티티로 변환 및 DB 저장/업데이트
    User user = saveOrUpdate(attributes, registrationId);

    // 6. Spring Security가 사용할 수 있는 OAuth2User 객체 반환
    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
        attributes,
        userNameAttributeName
    );
  }

  private User saveOrUpdate(Map<String, Object> attributes, String registrationId) {
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");

    User user = userRepository.findByEmail(email)
        .map(entity -> entity.update(name)) // 이미 가입된 유저면 이름 업데이트
        .orElse(User.builder() // 신규 유저면 생성
            .name(name)
            .email(email)
            .role(Role.USER) // 기본 권한 USER
            .provider(registrationId)
            .build());

    return userRepository.save(user);
  }

}
