package com.spring.minio.service;

import com.jlefebure.spring.boot.minio.MinioException;
import com.spring.minio.model.FileInfo;
import com.spring.minio.model.FileResponseDto;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    List<Item> listFiles() throws MinioException;
    FileResponseDto uploadFile(MultipartFile file);
    FileInfo getFile(String filename);
    void deleteFile(String filename);
}
