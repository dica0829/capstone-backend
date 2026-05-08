package com.zoopick.server.dto.chat;

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
