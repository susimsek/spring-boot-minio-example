package com.spring.minio.service.impl;


import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import com.spring.minio.exception.ResourceNotFoundException;
import com.spring.minio.mapper.FileMapper;
import com.spring.minio.model.FileInfo;
import com.spring.minio.model.FileResponseDto;
import com.spring.minio.service.FileService;
import io.minio.ObjectStat;
import io.minio.messages.Item;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileServiceImpl implements FileService {

    final MinioService minioService;
    final FileMapper fileMapper;

    @Override
    public List<Item> listFiles() throws MinioException {
        return minioService.fullList();
    }

    @Override
    public FileResponseDto uploadFile(MultipartFile file) {
        Path path = Path.of(file.getOriginalFilename());
        try {
            minioService.upload(path, file.getInputStream(), file.getContentType());

            ObjectStat metadata = minioService.getMetadata(path);
            return fileMapper.objectStatToFileResponseDto(metadata);
        } catch (MinioException e) {
            throw new IllegalStateException("The file cannot be upload on the internal storage. Please retry later", e);
        } catch (IOException e) {
            throw new IllegalStateException("The file cannot be read", e);
        }

    }

    @Override
    public FileInfo getFile(String filename) {
        Path path = Path.of(filename);
        try {
            ObjectStat metadata = minioService.getMetadata(path);
            return fileMapper.objectStatToFileInfo(metadata);

        } catch (MinioException e) {
            throw new ResourceNotFoundException("File", "filename", filename);
        }
        catch (IOException e) {
            throw new IllegalStateException("The file cannot be read", e);
        }

    }

    @Override
    public void deleteFile(String filename) {
        Path path = Path.of(filename);
        try {
            minioService.remove(path);
        } catch (MinioException e) {
            throw new ResourceNotFoundException("File", "filename", filename);
        }

    }
}
