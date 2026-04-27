package com.zoopick.server.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.zoopick.server.dto.fcm.FcmNotificationRequest;
import com.zoopick.server.entity.User;
import com.zoopick.server.exception.AccessTokenException;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public void register(String accessToken, String fcmToken) throws AccessTokenException, DataNotFoundException {
        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findBySchoolEmail(email)
                .orElseThrow(() -> new DataNotFoundException(DataNotFoundException.Subject.USER, email + " is not in UserRepository."));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    public String send(String targetNickname, FcmNotificationRequest request)
            throws DataNotFoundException, FirebaseMessagingException {
        User user = userRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new DataNotFoundException(DataNotFoundException.Subject.USER, targetNickname + " is not in UserRepository."));
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
