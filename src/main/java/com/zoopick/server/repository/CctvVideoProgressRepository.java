package com.zoopick.server.repository;

import com.zoopick.server.entity.CctvVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CctvVideoProgressRepository extends JpaRepository<CctvVideoProgress, Long> {
    Optional<CctvVideoProgress> findByCctvVideoId(Long videoId);
}
