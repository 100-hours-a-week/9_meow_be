package meow_be.eventposts.repository;

import meow_be.eventposts.domain.EventPost;
import meow_be.eventposts.domain.EventWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPostRepository extends JpaRepository<EventPost, Integer> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
            "FROM EventPost p WHERE p.user.id = :userId AND p.eventWeek.week = :week")
    boolean existsByUserIdAndWeek(@Param("userId") Integer userId, @Param("week") Integer week);

    List<EventPost> findTop3ByEventWeek_WeekOrderByCreatedAtDesc(int week);

}
