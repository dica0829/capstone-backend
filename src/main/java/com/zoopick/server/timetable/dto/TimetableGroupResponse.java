package com.zoopick.server.timetable.dto;

public record TimetableGroupResponse(
    Long timetableId,
    String name,
    Integer year,
    Integer semester,
    Boolean isPrimary
) {}
