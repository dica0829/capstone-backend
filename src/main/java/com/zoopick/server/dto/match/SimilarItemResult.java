package com.zoopick.server.dto.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimilarItemResult {

    private Long itemId;
    private double score;
}