package com.zoopick.server.dto.timetable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class TimetableCourseScheduleRecord {
    @JsonProperty("day_of_week")
    private DayOfWeek dayOfWeek;
    @JsonProperty("start_time")
    private LocalTime startTime;
    @JsonProperty("end_time")
    private LocalTime endTime;
}
