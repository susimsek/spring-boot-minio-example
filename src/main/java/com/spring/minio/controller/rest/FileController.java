package com.spring.minio.controller.rest;


import com.jlefebure.spring.boot.minio.MinioException;
import com.spring.minio.model.FileInfo;
import com.spring.minio.model.FileResponseDto;
import com.spring.minio.service.FileService;
import io.minio.messages.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Tag(name = "Files", description = "Retrieve and manage files")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class FileController {

    final FileService fileService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",content = @Content(array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content)

    })
    @Operation(summary = "Get all Files")
    @GetMapping(value = "/files")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> listFiles()throws MinioException {
        return fileService.listFiles();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content)

    })
    @Operation(summary = "Upload a File")
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    @ResponseStatus(HttpStatus.OK)
    public FileResponseDto uploadFile(@RequestPart(name = "file") @Parameter(description = "File to be uploaded", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) MultipartFile file) throws IOException {
        return fileService.uploadFile(file);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content)

    })
    @Operation(summary = "View existing File")
    @GetMapping("/files/{filename}/view")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> viewFile(@Parameter(description="Id of the File", required=true) @PathVariable String filename) {
        FileInfo fileInfo = fileService.getFile(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileInfo.getName() + "\"")
                .body(new ByteArrayResource(fileInfo.getData()));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content)

    })
    @Operation(summary = "Download existing File")
    @GetMapping("/files/{filename}/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadFile(@Parameter(description="Id of the File", required=true) @PathVariable String filename) {
        FileInfo fileInfo = fileService.getFile(filename);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(fileInfo.getType()))
                .contentLength(fileInfo.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getName() + "\"")
                .body(new ByteArrayResource(fileInfo.getData()));

    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content",content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content)

    })
    @Operation(summary = "Delete existing File")
    @DeleteMapping("/files/{filename}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@Parameter(description="Id of the File", required=true) @PathVariable String filename){
        fileService.deleteFile(filename);
    }

}
