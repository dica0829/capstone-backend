package com.zoopick.server.service;

import com.zoopick.server.dto.image.ImageUploadResult;
import com.zoopick.server.exception.BadRequestException;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.exception.InternalServerException;
import com.zoopick.server.image.ImagePurpose;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@NullMarked
public class ImageService {
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private final Path imageUploadRoot;

    public ImageService(@Value("${zoopick.upload.image-dir}") String uploadDirectory) {
        this.imageUploadRoot = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        for (ImagePurpose imagePurpose : ImagePurpose.values())
            Files.createDirectories(imagePurpose.getPath(imageUploadRoot));
    }

    public ImageUploadResult upload(ImagePurpose imagePurpose, MultipartFile image) {
        validateImage(image);
        String fileName = image.getOriginalFilename();
        if (fileName == null)
            throw new BadRequestException("잘못된 파일명입니다.");
        StringUtils.cleanPath(fileName);
        String extension = extractExtension(fileName);
        String storedFilename = UUID.randomUUID() + "." + extension;
        Path targetPath = imagePurpose.getPath(imageUploadRoot)
                .resolve(storedFilename).normalize();

        if (!targetPath.startsWith(imageUploadRoot))
            throw new BadRequestException("잘못된 파일명입니다.");

        try {
            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new InternalServerException("Failed to store the image: " + exception.getMessage(), exception);
        }

        return new ImageUploadResult(imagePurpose.getUrl() + storedFilename, fileName);
    }

    public Resource loadAsResource(ImagePurpose purpose, String filename) {
        try {
            Path purposePath = purpose.getPath(imageUploadRoot);
            Path filePath = purposePath.resolve(filename).normalize();
            if (!filePath.startsWith(purposePath))
                throw new BadRequestException("잘못된 이미지 경로입니다.");

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable())
                throw DataNotFoundException.from("이미지", filename);
            return resource;
        } catch (MalformedURLException exception) {
            throw new InternalServerException("이미지를 불러오지 못했습니다: " + exception.getMessage(), exception);
        }
    }

    private void validateImage(MultipartFile image) {
        if (image.isEmpty())
            throw new BadRequestException("이미지 파일이 비어 있습니다.");

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType))
            throw new BadRequestException("jpg, png, webp 형식의 이미지만 업로드할 수 있습니다.");

        String originalFilename = image.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains("."))
            throw new BadRequestException("확장자가 없는 파일은 업로드할 수 없습니다.");
    }

    private String extractExtension(String filename) {
        int extensionStartIndex = filename.lastIndexOf('.');
        if (extensionStartIndex < 0 || extensionStartIndex == filename.length() - 1)
            throw new BadRequestException("확장자가 없는 파일은 업로드할 수 없습니다.");
        return filename.substring(extensionStartIndex + 1).toLowerCase();
    }
}
