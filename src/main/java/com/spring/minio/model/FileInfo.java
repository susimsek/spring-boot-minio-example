package com.spring.minio.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileInfo {

    String name;
    String type;
    Long size;
    byte[] data;
    Date createdAt;
}
