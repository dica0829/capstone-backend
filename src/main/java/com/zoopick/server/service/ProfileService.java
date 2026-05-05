package com.zoopick.server.service;

import com.zoopick.server.dto.profile.ProfileSummaryResponse;
import com.zoopick.server.dto.profile.ProfileUpdateRequest;
import com.zoopick.server.entity.User;
import com.zoopick.server.repository.ChatRoomRepository;
import com.zoopick.server.repository.ItemPostRepository;
import com.zoopick.server.repository.NotificationRepository;
import com.zoopick.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final ItemPostRepository itemPostRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationRepository notificationRepository;

    // 내 프로필 요약 정보 조회
    public ProfileSummaryResponse getProfileSummary(String email) {
        User user = userRepository.findBySchoolEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Long userId = user.getId();

        long postCount = itemPostRepository.countByUserId(userId);
        long chatRoomCount = chatRoomRepository.countByOwnerIdOrFinderId(userId, userId);
        long unreadCount = notificationRepository.countByUserIdAndReadAtIsNull(userId);

        return new ProfileSummaryResponse(
                user.getNickname(),
                user.getDepartment(),
                postCount,
                chatRoomCount,
                unreadCount
        );
    }

    // 프로필 정보 수정
    @Transactional
    public void updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findBySchoolEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 닉네임 중복 체크 (본인의 현재 닉네임과 다른데 이미 존재할 경우)
        if (!user.getNickname().equals(request.nickname()) &&
                userRepository.existsByNickname(request.nickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        // 엔티티 내 업데이트 메서드 호출
        user.updateProfile(request.nickname(), request.department());
    }
}