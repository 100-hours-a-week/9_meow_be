package meow_be.users.repository;

import meow_be.posts.domain.PostImage;
import meow_be.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
