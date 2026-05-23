package com.zoopick.server.converter;

import com.zoopick.server.image.dto.ImagePurpose;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToImagePurposeConverter implements Converter<String, ImagePurpose> {
    @Override
    public ImagePurpose convert(String source) {
        return ImagePurpose.valueOf(source.trim().toUpperCase());
    }
}
