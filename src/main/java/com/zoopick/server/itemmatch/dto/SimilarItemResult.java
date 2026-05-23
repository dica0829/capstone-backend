package com.zoopick.server.itemmatch.dto;

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