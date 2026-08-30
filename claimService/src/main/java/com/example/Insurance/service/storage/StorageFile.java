package com.example.Insurance.service.storage;
import lombok.Data;
import lombok.Getter;

import java.io.InputStream;

public record StorageFile(

        InputStream inputStream,

        String fileName,

        String contentType,

        Long fileSize

) {
}
