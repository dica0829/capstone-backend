package com.zoopick.server.cctv.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetDetectionByItemIdResponse {
    List<CctvDetectionDetail> detections;
}
