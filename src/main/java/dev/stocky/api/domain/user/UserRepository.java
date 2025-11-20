package dev.stocky.api.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 어노테이션은 생략 가능, JpaRepository 상속 시 자동 인식됨
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email); // 이메일로 사용자 조회

}
