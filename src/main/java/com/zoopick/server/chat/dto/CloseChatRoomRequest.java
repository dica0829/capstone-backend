package com.zoopick.server.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CloseChatRoomRequest {
    @NotNull
    private ChatRoomCloseReason reason;
}
