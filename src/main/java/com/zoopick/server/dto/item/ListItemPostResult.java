package com.zoopick.server.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListItemPostResult {
    @NotBlank
    @JsonProperty("item_posts")
    private List<ItemPostRecord> itemPosts;
    @NotBlank
    private int total;
    @NotBlank
    private int page;
}
