package com.zoopick.server.timetable.dto;

import java.util.List;

public record TimetableSyncRequest(
    List<CourseColorRequest> courses
) {
    public record CourseColorRequest(
        Long courseId,
        String color
    ) {}
}
