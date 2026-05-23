package com.zoopick.server.profile.service;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.auth.repository.UserRepository;
import com.zoopick.server.chat.repository.ChatRoomRepository;
import com.zoopick.server.itempost.repository.ItemPostRepository;
import com.zoopick.server.notification.repository.NotificationRepository;
import com.zoopick.server.profile.dto.ProfileSummaryResponse;
import com.zoopick.server.profile.dto.ProfileUpdateRequest;
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

    public ProfileSummaryResponse getProfileSummary(String email) {
        // 공통 메서드 사용으로 DataNotFoundException 발생
        User user = userRepository.findBySchoolEmailOrThrow(email);
        Long userId = user.getId();

        long postCount = itemPostRepository.countByUserId(userId);
        long chatRoomCount = chatRoomRepository.countByOwnerIdOrFinderId(userId, userId);
        long unreadCount = notificationRepository.countByUserIdAndReadAtIsNull(userId);

        return new ProfileSummaryResponse(
                userId,
                user.getNickname(),
                user.getDepartment(),
                postCount,
                chatRoomCount,
                unreadCount
        );
    }

    public String findNickname(long userId) {
        User user = userRepository.findByIdOrThrow(userId);
        return user.getNickname();
    }

    @Transactional
    public void updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findBySchoolEmailOrThrow(email);

        // 닉네임 중복 체크
        if (!user.getNickname().equals(request.nickname()) &&
                userRepository.existsByNickname(request.nickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        user.updateProfile(request.nickname(), request.department());
    }
}