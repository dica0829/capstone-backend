package com.zoopick.server.service;

import com.zoopick.server.auth.entity.Role;
import com.zoopick.server.auth.entity.User;
import com.zoopick.server.auth.repository.UserRepository;
import com.zoopick.server.cctv.entity.CctvDetection;
import com.zoopick.server.cctv.entity.CctvDetectionMatch;
import com.zoopick.server.cctv.entity.CctvVideo;
import com.zoopick.server.cctv.event.CreateCctvMatchEvent;
import com.zoopick.server.cctv.event.CreateCctvMatchEventListner;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.item.entity.ItemType;
import com.zoopick.server.itempost.entity.ItemPost;
import com.zoopick.server.metadata.entity.Room;
import com.zoopick.server.notification.SendNotificationCommand;
import com.zoopick.server.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCctvMatchEventListnerTest {

    @Mock NotificationService notificationService;
    @Mock UserRepository userRepository;

    @InjectMocks
    CreateCctvMatchEventListner listener;

    User reporter;
    Item item;
    CctvDetectionMatch match;
    ItemPost itemPost;
    Room room;
    CreateCctvMatchEvent event;

    @BeforeEach
    void setUp() {
        reporter = User.builder()
                .id(1L).schoolEmail("user@test.com").password("pw")
                .nickname("user").role(Role.STUDENT).build();
        item = Item.builder()
                .id(10L).reporter(reporter).type(ItemType.LOST).build();
        CctvVideo cctvVideo = CctvVideo.builder()
                .id(1L)
                .room(Room.builder().id(10L).name("101호").build())
                .recordedAt(LocalDateTime.now().minusHours(1))
                .durationSeconds(300)
                .videoUrl("cctv/videos/test.mp4")
                .build();
        CctvDetection cctvDetection = CctvDetection.builder()
                .id(1L).cctvVideo(cctvVideo)
                .detectedAt(LocalDateTime.now())
                .detectedCategory(ItemCategory.WALLET)
                .build();
        match = CctvDetectionMatch.builder()
                .id(100L).item(item).cctvDetection(cctvDetection).score(0.9f).build();
        itemPost = ItemPost.builder().id(1L).item(item).title("지갑 잃어버렸어요").build();
        room = Room.builder().id(10L).name("101호").build();

        event = new CreateCctvMatchEvent(List.of(new CreateCctvMatchEvent.Entry(
                match.getId(),
                match.getScore(),
                item.getId(),
                reporter.getId(),
                itemPost.getTitle(),
                room.getName()
        )));
    }

    @Test
    @DisplayName("매칭 이벤트 수신 시 신고자에게 FCM 알림을 전송한다")
    void handleMatchCreated_sendsNotificationToReporter() {
        given(userRepository.findAllById(any())).willReturn(List.of(reporter));

        listener.handleMatchCreated(event);

        then(notificationService).should().send(eq(reporter), any(SendNotificationCommand.class));
    }

    @Test
    @DisplayName("알림 제목에 '도난 의심'이 포함된다")
    void handleMatchCreated_notificationTitleContainsTheftSuspected() {
        given(userRepository.findAllById(any())).willReturn(List.of(reporter));
        ArgumentCaptor<SendNotificationCommand> captor = ArgumentCaptor.forClass(SendNotificationCommand.class);

        listener.handleMatchCreated(event);

        then(notificationService).should().send(eq(reporter), captor.capture());
        assertThat(captor.getValue().title()).isEqualTo("도난 의심");
    }

    @Test
    @DisplayName("알림 본문에 게시글 제목과 장소명이 포함된다")
    void handleMatchCreated_notificationBodyContainsTitleAndRoom() {
        given(userRepository.findAllById(any())).willReturn(List.of(reporter));
        ArgumentCaptor<SendNotificationCommand> captor = ArgumentCaptor.forClass(SendNotificationCommand.class);

        listener.handleMatchCreated(event);

        then(notificationService).should().send(eq(reporter), captor.capture());
        assertThat(captor.getValue().body())
                .contains(itemPost.getTitle())
                .contains(room.getName());
    }
}
