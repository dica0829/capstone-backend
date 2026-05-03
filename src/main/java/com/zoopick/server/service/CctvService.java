package com.zoopick.server.service;

import com.zoopick.server.config.FastApiProperties;
import com.zoopick.server.dto.cctv.*;
import com.zoopick.server.entity.CctvDetection;
import com.zoopick.server.entity.CctvVideo;
import com.zoopick.server.entity.CctvVideoProgress;
import com.zoopick.server.entity.VideoAnalysisStatus;
import com.zoopick.server.exception.BadRequestException;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.repository.CctvDetectionRepository;
import com.zoopick.server.repository.CctvVideoProgressRepository;
import com.zoopick.server.repository.CctvVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvService {
    private final CctvVideoRepository cctvVideoRepository;
    private final CctvVideoProgressRepository cctvVideoProgressRepository;
    private final CctvDetectionRepository cctvDetectionRepository;
    private final RestClient fastApiRestClient;
    private final FastApiProperties fastApiProperties;

    @Value("${zoopick.callback-url}")
    private String callbackBaseUrl;

    @Value("${zoopick.cctv.snapshot-dir}")
    private String snapshotBasePath;

    @Transactional
    public CctvEnqueueResponse enqueueVideo(Long videoId) {
        CctvVideo video = cctvVideoRepository.findById(videoId)
                .orElseThrow(() -> new DataNotFoundException("비디오를 찾을 수 없습니다. ID: " + videoId, "VIDEO_NOT_FOUND"));

        // Progress 정보 확인 및 중복 분석 방지
        CctvVideoProgress progress = cctvVideoProgressRepository.findByCctvVideoId(videoId).orElse(null);
        
        if (progress != null) {
            if (progress.getStatus() == VideoAnalysisStatus.COMPLETED) {
                throw new BadRequestException("이미 분석이 완료된 비디오입니다. ID: " + videoId, "ALREADY_COMPLETED");
            } else if (progress.getStatus() == VideoAnalysisStatus.PENDING || progress.getStatus() == VideoAnalysisStatus.IN_PROGRESS) {
                throw new BadRequestException("이미 분석이 진행 중인 비디오입니다. ID: " + videoId, "ALREADY_PROCESSING");
            }
            // FAILED인 경우 재시도를 위해 상태 초기화
            progress.setStatus(VideoAnalysisStatus.PENDING);
            progress.setAnalyzedSeconds(0);
            progress.setProgressPercent(0.0);
        } else {
            progress = CctvVideoProgress.builder()
                    .cctvVideo(video)
                    .status(VideoAnalysisStatus.PENDING)
                    .totalDurationSeconds(video.getDurationSeconds())
                    .build();
        }
        cctvVideoProgressRepository.save(progress);

        // FastAPI 요청 DTO 생성
        CctvEnqueueRequest request = CctvEnqueueRequest.builder()
                .videoId(videoId)
                .videoPath(video.getVideoUrl()) // DB의 video_url이 절대 경로라고 가정
                .durationSeconds(progress.getTotalDurationSeconds())
                .recordedAt(video.getRecordedAt())
                .callbackBaseUrl(callbackBaseUrl)
                .build();

        String url = fastApiProperties.getBaseUrl() + fastApiProperties.getCctv().getEnqueuePath();

        try {
            CctvEnqueueResponse response = fastApiRestClient.post()
                    .uri(url)
                    .body(request)
                    .retrieve()
                    .body(CctvEnqueueResponse.class);

            if (response != null && response.isQueued()) {
                log.info("CCTV Video analysis queued successfully: video_id={}", videoId);
            }
            return response;
        } catch (Exception e) {
            log.error("Failed to enqueue CCTV video analysis: video_id={}, error={}", videoId, e.getMessage());
            throw new BadRequestException("FastAPI 서버에 분석 요청을 보내지 못했습니다.", e.getMessage());
        }
    }

    @Transactional
    public void updateProgress(CctvProgressCallback callback) {
        CctvVideoProgress progress = cctvVideoProgressRepository.findByCctvVideoId(callback.getVideoId())
                .orElseThrow(() -> new BadRequestException("진행 정보를 찾을 수 없습니다. video_id: " + callback.getVideoId(),
                        "PROGRESS_NOT_FOUND"));

        progress.setStatus(VideoAnalysisStatus.valueOf(callback.getStatus()));
        progress.setAnalyzedSeconds(callback.getAnalyzedSeconds());
        progress.setProgressPercent(callback.getProgressPercent());
        progress.setEstimatedCompletionAt(callback.getEstimatedCompletionAt());

        if (progress.getStatus() == VideoAnalysisStatus.IN_PROGRESS && progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
        }

        cctvVideoProgressRepository.save(progress);
        log.debug("CCTV Progress updated: video_id={}, progress={}%, status={}",
                callback.getVideoId(), callback.getProgressPercent(), callback.getStatus());
    }

    @Transactional
    public void registerDetection(CctvDetectionCallback callback) {
        CctvVideo video = cctvVideoRepository.findById(callback.getVideoId())
                .orElseThrow(() -> new DataNotFoundException("비디오를 찾을 수 없습니다. video_id: " + callback.getVideoId(),
                        "VIDEO_NOT_FOUND"));

        String itemUrl = snapshotBasePath + callback.getItemSnapshotFilename();
        String momentUrl = snapshotBasePath + callback.getMomentSnapshotFilename();

        CctvDetection detection = CctvDetection.builder()
                .cctvVideo(video)
                .detectedAt(callback.getDetectedAt())
                .detectedCategory(callback.getDetectedCategory())
                .detectedColor(callback.getDetectedColor())
                .itemSnapshotUrl(itemUrl)
                .momentSnapshotUrl(momentUrl)
                .embedding(callback.getEmbedding())
                .build();

        cctvDetectionRepository.save(detection);
        log.info("New detection registered: video_id={}, ai_detection_id={}",
                callback.getVideoId(), callback.getDetectionId());
    }

    @Transactional
    public void completeAnalysis(CctvCompletedCallback callback) {
        CctvVideoProgress progress = cctvVideoProgressRepository.findByCctvVideoId(callback.getVideoId())
                .orElseThrow(() -> new DataNotFoundException("진행 정보를 찾을 수 없습니다. video_id: " + callback.getVideoId(),
                        "PROGRESS_NOT_FOUND"));

        progress.setStatus(VideoAnalysisStatus.COMPLETED);
        progress.setAnalyzedSeconds(callback.getTotalSeconds());
        progress.setProgressPercent(100.0);
        progress.setStartedAt(callback.getStartedAt());

        cctvVideoProgressRepository.save(progress);
        log.info("CCTV Video analysis completed: video_id={}, total_detections={}",
                callback.getVideoId(), callback.getTotalDetections());

        // TODO: 매칭 트리거 및 알림 로직 추가
    }

    @Transactional
    public void failAnalysis(CctvFailedCallback callback) {
        CctvVideoProgress progress = cctvVideoProgressRepository.findByCctvVideoId(callback.getVideoId())
                .orElseThrow(() -> new DataNotFoundException("진행 정보를 찾을 수 없습니다. video_id: " + callback.getVideoId(),
                        "PROGRESS_NOT_FOUND"));

        progress.setStatus(VideoAnalysisStatus.FAILED);
        progress.setAnalyzedSeconds(callback.getAnalyzedSeconds());

        cctvVideoProgressRepository.save(progress);
        log.error("CCTV Video analysis failed: video_id={}, error_code={}, error_message={}",
                callback.getVideoId(), callback.getErrorCode(), callback.getErrorMessage());
    }
}
