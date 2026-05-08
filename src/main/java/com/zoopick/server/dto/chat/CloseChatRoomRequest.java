package com.zoopick.server.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CloseChatRoomRequest {
    @NotBlank
    private ChatRoomCloseReason reason;
}
