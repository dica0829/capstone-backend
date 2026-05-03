package com.zoopick.server.repository;

import com.zoopick.server.entity.CctvVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CctvVideoRepository extends JpaRepository<CctvVideo, Long> {
}
