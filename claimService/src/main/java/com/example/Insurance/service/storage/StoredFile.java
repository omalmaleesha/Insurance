package com.example.Insurance.service.storage;
import com.example.Insurance.utils.types.StorageProvider;

public record StoredFile(

        StorageProvider provider,

        String fileId,

        String folderId,

        String storedFileName,

        String contentType,

        Long fileSize

) {
}