package com.zoopick.server.service;

import com.zoopick.server.entity.*;
import com.zoopick.server.repository.CourseScheduleRepository;
import com.zoopick.server.repository.RoomRepository;
import com.zoopick.server.repository.TimetableGroupRepository;
import com.zoopick.server.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CctvMatchCriteriaResolver {
    private final TimetableGroupRepository timetableGroupRepository;
    private final TimetableRepository timetableRepository;
    private final CourseScheduleRepository courseScheduleRepository;
    private final RoomRepository roomRepository;

    /**
     * 분실물의 CCTV 매칭 후보 강의실 ID들을 결정한다.
     *
     * - 신고 건물/시각 정보가 없으면 빈 리스트 (매칭 스킵)
     * - 신고 시각에 reporter 시간표상 진행 중인 강의실 set에 신고 위치가 포함되면 → 그 강의실들로 좁힘
     * - 그 외 → 건물 전체 강의실
     */
    public List<Long> resolveRoomIds(Item lostItem) {
        if (lostItem.getReportedBuilding() == null || lostItem.getReportedAt() == null) {
            return List.of();
        }

        Set<Long> scheduleRoomIds = findScheduleRoomIdsAt(lostItem.getReporter(),
                lostItem.getReportedBuilding(),
                lostItem.getReportedAt());

        if (isReportedRoomInSchedule(lostItem, scheduleRoomIds)) {
            return List.copyOf(scheduleRoomIds);
        }

        return findAllRoomIdsInBuilding(lostItem.getReportedBuilding());
    }

    // 해당 시각에 reporter 시간표상 진행 중인 (해당 건물의) 강의실 ID set
    private Set<Long> findScheduleRoomIdsAt(User reporter, Building building, LocalDateTime reportedTime) {
        var primaryGroup = timetableGroupRepository.findByUserAndIsPrimaryTrue(reporter);
        if (primaryGroup.isEmpty()) {
            return Set.of();
        }

        List<Course> buildingCourses = timetableRepository.findAllByTimetableGroup(primaryGroup.get()).stream()
                .map(Timetable::getCourse)
                .filter(c -> c.getRoom().getBuilding().getId().equals(building.getId()))
                .toList();

        if (buildingCourses.isEmpty()) {
            return Set.of();
        }

        DayOfWeek targetDay = DayOfWeek.valueOf(reportedTime.getDayOfWeek().name().substring(0, 3));
        LocalTime targetLocalTime = reportedTime.toLocalTime();

        return courseScheduleRepository.findAllByCourseIn(buildingCourses).stream()
                .filter(s -> s.getDayOfWeek() == targetDay &&
                        !targetLocalTime.isBefore(s.getStartTime()) &&
                        !targetLocalTime.isAfter(s.getEndTime()))
                .map(s -> s.getCourse().getRoom().getId())
                .collect(Collectors.toSet());
    }

    // 신고 글의 locationName이 가리키는 강의실이 시간표 set 안에 있는지
    private boolean isReportedRoomInSchedule(Item lostItem, Set<Long> scheduleRoomIds) {
        if (scheduleRoomIds.isEmpty()) {
            return false;
        }
        return roomRepository.findByNameAndBuilding(lostItem.getLocationName(), lostItem.getReportedBuilding())
                .map(room -> scheduleRoomIds.contains(room.getId()))
                .orElse(false);
    }

    private List<Long> findAllRoomIdsInBuilding(Building building) {
        return roomRepository.findAllByBuilding(building).stream()
                .map(Room::getId)
                .toList();
    }
}
