package meow_be.eventposts.repository;

import meow_be.eventposts.domain.EventWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventWeekRepository extends JpaRepository<EventWeek, Integer> {

    @Query("SELECT e FROM EventWeek e WHERE " +
            ":datetime BETWEEN e.startApplyAt AND e.endEventAt")
    Optional<EventWeek> findByDate(LocalDateTime datetime);
}
