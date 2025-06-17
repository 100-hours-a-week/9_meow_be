package meow_be.eventposts.repository;

import meow_be.eventposts.domain.EventPost;
import meow_be.eventposts.domain.EventWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPostRepository extends JpaRepository<EventPost, Integer> {
    List<EventPost> findAllByEventWeek(EventWeek eventWeek);

}
