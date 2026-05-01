package com.zoopick.server.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemResponse {
    private String title;

    @JsonProperty("item_id")

    private Long itemId;

    private String status;

    private String message;
}
