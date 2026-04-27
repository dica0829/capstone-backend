package com.zoopick.server.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.zoopick.server.dto.fcm.FcmNotificationRequest;
import com.zoopick.server.entity.User;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public void register(String accessToken, String fcmToken) {
        String email = jwtUtil.extractEmail(accessToken);
        if (email == null)
            throw new IllegalStateException("세션이 유효하지 않습니다.");
        User user = userRepository.findBySchoolEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. | " + email));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    public String send(String targetNickname, FcmNotificationRequest request) throws FirebaseMessagingException {
        User user = userRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. | " + targetNickname));
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();
        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(request.getData())
                .setToken(user.getFcmToken())
                .build();
        return FirebaseMessaging.getInstance().send(message);
    }
}
