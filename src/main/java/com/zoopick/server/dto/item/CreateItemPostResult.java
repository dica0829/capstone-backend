package com.zoopick.server.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.entity.ItemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateItemPostResult {
    @NotBlank
    private long itemId;
    @NotBlank
    @JsonProperty("item_status")
    private ItemStatus itemStatus;
    private String message;
}
