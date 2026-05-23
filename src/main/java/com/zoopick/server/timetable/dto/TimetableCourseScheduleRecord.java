package com.zoopick.server.timetable.dto;

import com.zoopick.server.timetable.entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class TimetableCourseScheduleRecord {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
