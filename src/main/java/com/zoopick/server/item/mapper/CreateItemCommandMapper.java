package com.zoopick.server.item.mapper;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.item.CreateItemCommand;
import com.zoopick.server.itempost.dto.CreateItemPostRequest;
import com.zoopick.server.metadata.entity.Building;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@Component
@NullMarked
public class CreateItemCommandMapper {
    public CreateItemCommand toCreateItemCommand(User user, Building building, CreateItemPostRequest request) {
        return CreateItemCommand.builder()
                .reporter(user)
                .type(request.getType())
                .category(request.getCategory())
                .color(request.getColor())
                .imageUrl(request.getImageUrl())
                .building(building)
                .detailAddress(request.getDetailAddress())
                .reportedAd(request.getReportedAt())
                .build();
    }

}
