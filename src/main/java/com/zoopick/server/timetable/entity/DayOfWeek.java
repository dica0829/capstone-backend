package com.zoopick.server.timetable.entity;

public enum DayOfWeek {
    MON, TUE, WED, THU, FRI, SAT, SUN;

    public boolean matches(java.time.DayOfWeek dayOfWeek) {
        return this == from(dayOfWeek);
    }

    public static DayOfWeek from(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> MON;
            case TUESDAY -> TUE;
            case WEDNESDAY -> WED;
            case THURSDAY -> THU;
            case FRIDAY -> FRI;
            case SATURDAY -> SAT;
            case SUNDAY -> SUN;
        };
    }
}
