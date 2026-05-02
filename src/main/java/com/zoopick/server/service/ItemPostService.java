package com.zoopick.server.service;

import com.zoopick.server.dto.item.*;
import com.zoopick.server.entity.*;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.mapper.ItemPostMapper;
import com.zoopick.server.repository.BuildingRepository;
import com.zoopick.server.repository.ItemPostRepository;
import com.zoopick.server.repository.ItemRepository;
import com.zoopick.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@NullMarked
public class ItemPostService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemPostRepository itemPostRepository;
    private final BuildingRepository buildingRepository;
    private final ItemPostMapper itemPostMapper;

    public CreateItemPostResult createItemPost(CreateItemPostRequest request, String email) {
        User user = userRepository.findBySchoolEmail(email)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다.", email + " is not a valid user."));
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new DataNotFoundException("건물 정보가 없습니다.", request.getBuildingId() + " is not a valid building."));
        Item item = Item.builder()
                .reporter(user)
                .type(request.getType())
                .status(ItemStatus.REPORTED)
                .category(request.getCategory())
                .color(request.getColor())
                .reportedBuilding(building)
                .locationName(request.getDetailAddress())
                .imageUrl(request.getImageUrl())
                .reportedAt(LocalDateTime.now())
                .build();

        Item savedItem = itemRepository.save(item);

        ItemPost itemPost = ItemPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .item(savedItem)
                .build();
        ItemPost savedItemPost = itemPostRepository.save(itemPost);

        return new CreateItemPostResult(savedItemPost.getId(), savedItem.getStatus(), "등록되었습니다.");
    }

    public ListItemPostResult getItemPosts(@Nullable ItemPostFilter filter, Pageable pageable) {
        Page<ItemPost> page = itemPostRepository.findAll(ItemPostRepository.applyFilter(filter), pageable);
        List<ItemPostRecord> itemPostRecords = page.stream().map(itemPostMapper::toItemPostRecord)
                .toList();

        return new ListItemPostResult(itemPostRecords, itemPostRecords.size(), page.getNumber());
    }

    public ItemPostRecord getItemPost(long id) {
        ItemPost itemPost = itemPostRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("게시물을 찾을 수 없습니다.", id + " is not a valid item post id."));
        return itemPostMapper.toItemPostRecord(itemPost);
    }
}
