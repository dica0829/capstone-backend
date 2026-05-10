package com.zoopick.server.dto.timetable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TimetableCourseRecord {
    @JsonProperty("course_id")
    private Long courseId;
    @JsonProperty("course_name")
    private String courseName;
    @JsonProperty("room_name")
    private String roomName;
    @JsonProperty("building_name")
    private String buildingName;
    @JsonProperty("building_code")
    private String buildingCode;
    private String color;
    private List<TimetableCourseScheduleRecord> schedules;
}
