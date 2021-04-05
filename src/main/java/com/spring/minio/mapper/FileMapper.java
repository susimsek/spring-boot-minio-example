package com.spring.minio.mapper;


import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import com.spring.minio.model.FileInfo;
import com.spring.minio.model.FileResponseDto;
import io.minio.ObjectStat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileMapper {

    final MinioService minioService;

    public FileInfo objectStatToFileInfo(ObjectStat metadata) throws IOException {

        Path path = Path.of(metadata.name());
        InputStream inputStream = null;
        try {
            inputStream = minioService.get(path);

            return FileInfo.builder()
                    .name(metadata.name())
                    .data(inputStream.readAllBytes())
                    .size(metadata.length())
                    .type(metadata.contentType())
                    .createdAt(metadata.createdTime())
                    .build();

        } catch (MinioException e) {
            throw new IllegalStateException("The file cannot be read on the internal storage. Please retry later", e);
        }
    }

    public FileResponseDto objectStatToFileResponseDto(ObjectStat metadata) throws IOException {
            return FileResponseDto.builder()
                    .name(metadata.name())
                    .size(metadata.length())
                    .type(metadata.contentType())
                    .createdAt(metadata.createdTime())
                    .build();
    }


}
