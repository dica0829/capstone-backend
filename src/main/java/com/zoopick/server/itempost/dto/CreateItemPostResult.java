package com.zoopick.server.itempost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.item.entity.ItemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateItemPostResult {
    @NotBlank
    @JsonProperty("item_post_id")
    private long itemPostId;
    @NotBlank
    @JsonProperty("item_id")
    private long itemId;
    @NotBlank
    @JsonProperty("item_status")
    private ItemStatus itemStatus;
    private String message;
}
