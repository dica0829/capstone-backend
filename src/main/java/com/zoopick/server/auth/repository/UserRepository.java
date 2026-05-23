package com.zoopick.server.auth.repository;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.metadata.entity.Building;
import com.zoopick.server.timetable.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    default User findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> DataNotFoundException.from("사용자", id));
    }

    Optional<User> findBySchoolEmail(String schoolEmail);

    default User findBySchoolEmailOrThrow(String schoolEmail) {
        return findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> DataNotFoundException.from("사용자", schoolEmail));
    }

    Optional<User> findByNickname(String nickname);

    default User findByNicknameOrThrow(String nickname) {
        return findByNickname(nickname)
                .orElseThrow(() -> DataNotFoundException.from("사용자", nickname));
    }

    boolean existsByNickname(String nickname);

    @Query("""
            select distinct tg.user
            from TimetableGroup tg
            join Timetable t on t.timetableGroup = tg
            join CourseSchedule cs on cs.course = t.course
            join t.course.room r
            where tg.isPrimary = true
              and tg.user <> :excludedUser
              and r.building = :building
              and cs.dayOfWeek = :dayOfWeek
              and cs.startTime < :time
              and cs.endTime > :time
            """)
    List<User> findUsersWithPrimaryTimetableAt(
            @Param("building") Building building,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time,
            @Param("excludedUser") User excludedUser
    );

}
