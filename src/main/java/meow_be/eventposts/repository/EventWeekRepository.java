package meow_be.eventposts.repository;

import meow_be.eventposts.domain.EventWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventWeekRepository extends JpaRepository<EventWeek, Integer> {

    @Query(value = "SELECT * FROM event_week e WHERE :now BETWEEN e.start_apply_at AND DATE_ADD(e.start_apply_at, INTERVAL 7 DAY)", nativeQuery = true)
    Optional<EventWeek> findByDate(@Param("now") LocalDateTime now);

}
