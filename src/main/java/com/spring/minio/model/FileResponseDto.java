package com.spring.minio.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponseDto {

    String name;
    String type;
    Long size;
    Date createdAt;
}
