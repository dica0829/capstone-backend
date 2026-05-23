package com.zoopick.server.metadata.repository;

import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.metadata.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    default Building findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> DataNotFoundException.from("건물", id));
    }
}
