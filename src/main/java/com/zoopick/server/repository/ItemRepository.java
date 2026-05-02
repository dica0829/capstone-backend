package com.zoopick.server.repository;

import com.zoopick.server.entity.Item;
import com.zoopick.server.exception.DataNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    default Item findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> DataNotFoundException.from("분실물", id));
    }
}
