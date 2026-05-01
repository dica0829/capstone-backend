package com.zoopick.server.controller;

import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.dto.item.ItemDetailResponse;
import com.zoopick.server.dto.item.ItemRequest;
import com.zoopick.server.dto.item.ItemResponse;
import com.zoopick.server.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Item API", description = "분실물/습득물 관리 API")
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "분실물/습득물 등록", description = "새로운 분실물이나 습득물을 신고합니다.")
    @PostMapping
    public ResponseEntity<CommonResponse<ItemResponse>> createItem(@RequestBody @Valid ItemRequest request) {
        ItemResponse responseData = itemService.registerItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(responseData));
    }

    @Operation(summary = "분실물/습득물 목록 조회", description = "등록된 모든 물품 목록을 가져옵니다.")
    @GetMapping
    public ResponseEntity<CommonResponse<List<ItemResponse>>> getAllItems() {
        List<ItemResponse> items = itemService.getAllItems();
        return ResponseEntity.ok(CommonResponse.success(items));
    }

    // GET /api/items/:id 엔드포인트 신규 생성
    @Operation(summary = "분실물/습득물 상세 조회", description = "특정 ID를 가진 물품의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ItemDetailResponse>> getItemById(@PathVariable Long id) {
        ItemDetailResponse responseData = itemService.getItemById(id);
        return ResponseEntity.ok(CommonResponse.success(responseData));
    }
}