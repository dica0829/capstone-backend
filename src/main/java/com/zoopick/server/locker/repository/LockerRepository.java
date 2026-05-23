package com.zoopick.server.locker.repository;

import com.zoopick.server.item.entity.Item;
import com.zoopick.server.locker.entity.Locker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerRepository extends JpaRepository<Locker, Long> {
    Locker findLockerByCurrentItem(Item currentItem);
}
