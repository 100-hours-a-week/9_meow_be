package meow_be.users.repository;

import meow_be.users.domain.Token;
import meow_be.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByUser(User user);
}
