package meow_be.users.repository;

import meow_be.users.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserId(int userId);
    Optional<Token> findByRefreshToken(String refreshToken);
    void deleteByUserId(Integer userId);

}
