package meow_be.users.repository;

import meow_be.users.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserId(int userId);
    Optional<Token> findByRefreshToken(String refreshToken);
    @Modifying
    @Query(value = "UPDATE Token t SET t.accessToken = '', t.refreshToken = '' WHERE t.user_id = :userId", nativeQuery = true)
    void deleteByUserIdNative(@Param("userId") int userId);

}
