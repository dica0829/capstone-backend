package com.zoopick.server.controller;

import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.dto.image.ImageUploadResult;
import com.zoopick.server.image.ImagePurpose;
import com.zoopick.server.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Image API", description = "이미지 업로드 및 조회 API")
@RestController
@RequiredArgsConstructor
@NullMarked
public class ImageController {
    private final ImageService imageService;

    @Operation(summary = "이미지 업로드", description = "용도별 이미지를 업로드하고 접근 가능한 URL을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 요청값")
    })
    @PostMapping(value = "/api/images/upload/{purpose}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ImageUploadResult>> uploadImage(
            @Parameter(description = "이미지 업로드 용도", example = "item")
            @PathVariable ImagePurpose purpose,
            @Parameter(
                    description = "업로드할 이미지 파일",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("image") MultipartFile image
    ) {
        ImageUploadResult result = imageService.upload(purpose, image);
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    @Operation(summary = "이미지 조회", description = "저장된 이미지 파일을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    @GetMapping("/images/{purpose}/{filename}")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "이미지 업로드 용도", example = "item")
            @PathVariable ImagePurpose purpose,
            @Parameter(description = "저장된 이미지 파일명", example = "550e8400-e29b-41d4-a716-446655440000.jpg")
            @PathVariable String filename
    ) {
        Resource resource = imageService.loadAsResource(purpose, filename);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .body(resource);
    }
}
