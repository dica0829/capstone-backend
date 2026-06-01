package com.zoopick.server.notification.dto;


import com.zoopick.server.notification.entity.NotificationType;
import com.zoopick.server.notification.payload.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "관리자 알림 전송 요청")
public class SendNotificationRequest {
    @Schema(description = "알림 제목", example = "새 매칭이 발견되었어요")
    private String title;

    @Schema(description = "알림 본문", example = "회원님의 분실물과 유사한 습득물이 등록되었습니다.")
    private String body;

    @NotNull
    @Schema(description = "알림 타입", example = "MATCH_FOUND")
    private NotificationType type;

    @Schema(
            description = "알림 타입별 payload입니다. type 값에 맞는 JSON 구조를 사용해야 합니다.",
            oneOf = {
                    MatchFoundPayload.class,
                    ChatMessagePayload.class,
                    ItemReturnedPayload.class,
                    TheftSuspectedPayload.class,
                    LockerReadyPayload.class
            }
    )
    @Builder.Default
    private Map<String, Object> payload = Map.of();
}
