package com.zoopick.server.qr.service;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.auth.repository.UserRepository;
import com.zoopick.server.qr.QrCodeGenerator;
import com.zoopick.server.qr.repository.QrCodeCacheRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@NullMarked
public class QrCodeService {
    private final UserRepository userRepository;
    private final QrCodeGenerator qrCodeGenerator;
    private final QrCodeCacheRepository qrCodeCacheRepository;
    @Value("${zoopick.base-url}")
    private String baseUrl;

    private String createUserQrContent(User user) {
        return baseUrl + "/scan/owner/" + user.getId();
    }

    public byte[] createUserQrCode(long userId) {
        User user = userRepository.findByIdOrThrow(userId);
        String content = createUserQrContent(user);
        return createQRCode(content);
    }

    public byte[] createQRCode(String content) {
        return qrCodeCacheRepository.computeIfAbsent(content, qrCodeGenerator::generate);
    }
}
