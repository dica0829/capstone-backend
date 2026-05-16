package com.zoopick.server.repository;

import com.zoopick.server.dto.cctv.CctvDetectionDetail;
import com.zoopick.server.entity.CctvDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CctvDetectionRepository extends JpaRepository<CctvDetection, Long> {
    List<CctvDetection> findAllByOrderByDetectedAtAsc();

    @Query("""
     SELECT new com.zoopick.server.dto.cctv.CctvDetectionDetail(
         d.id,
         m.score,
         d.detectedAt,
         b.name,
         r.name,
         d.itemSnapshotUrl,
         d.momentSnapshotUrl
     )
     FROM CctvDetectionMatch m
     JOIN m.item i
     JOIN m.cctvDetection d
     JOIN d.cctvVideo v
     JOIN v.room r
     JOIN r.building b
     WHERE i.id = :itemId
       AND i.reporter.id = :userId
""")
    List<CctvDetectionDetail> findCctvDetectionDetail(Long userId, Long itemId);
}
