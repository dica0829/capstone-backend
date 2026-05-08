package com.zoopick.server.converter;

import com.zoopick.server.dto.chat.ChatRoomCloseReason;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCloseReasonConverter implements Converter<String, ChatRoomCloseReason> {
    @Override
    public ChatRoomCloseReason convert(String source) {
        return ChatRoomCloseReason.valueOf(source.trim().toUpperCase());
    }
}
