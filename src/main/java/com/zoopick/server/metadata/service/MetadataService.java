package com.zoopick.server.metadata.service;

import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.metadata.dto.BuildingRecord;
import com.zoopick.server.metadata.dto.RoomRecord;
import com.zoopick.server.metadata.entity.Building;
import com.zoopick.server.metadata.repository.BuildingRepository;
import com.zoopick.server.metadata.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@NullMarked
public class MetadataService {
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;

    public List<BuildingRecord> getBuildings() {
        return buildingRepository.findAll().stream()
                .map(b -> new BuildingRecord(b.getId(), b.getName()))
                .toList();
    }

    public List<RoomRecord> getRooms(Long buildingId) {
        Building building = buildingRepository.findByIdOrThrow(buildingId);
        return roomRepository.findAllByBuilding(building).stream()
                .map(r -> new RoomRecord(r.getId(), r.getName()))
                .toList();
    }

    public List<String> getItemCategories() {
        return Arrays.stream(ItemCategory.values())
                .map(ItemCategory::name)
                .toList();
    }
}
