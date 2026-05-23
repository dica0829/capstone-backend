package com.zoopick.server.timetable.repository;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.timetable.entity.TimetableGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimetableGroupRepository extends JpaRepository<TimetableGroup, Long> {
    List<TimetableGroup> findAllByUserAndYearAndSemester(User user, Integer year, Integer semester);
    List<TimetableGroup> findAllByUserOrderByYearDescSemesterDesc(User user);
    Optional<TimetableGroup> findByIdAndUser(Long id, User user);
    Optional<TimetableGroup> findByUserAndIsPrimaryTrue(User user);
}
