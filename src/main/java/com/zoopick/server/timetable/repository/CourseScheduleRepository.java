package com.zoopick.server.timetable.repository;

import com.zoopick.server.timetable.entity.Course;
import com.zoopick.server.timetable.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {
    List<CourseSchedule> findAllByCourseIn(List<Course> courses);

    List<CourseSchedule> findAllByCourseIdIn(List<Long> courseIds);
}
