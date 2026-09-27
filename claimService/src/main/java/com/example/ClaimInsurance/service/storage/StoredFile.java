package com.example.ClaimInsurance.service.storage;
import com.example.ClaimInsurance.utils.types.StorageProvider;

public record StoredFile(

        StorageProvider provider,

        String fileId,

        String folderId,

        String storedFileName,

        String contentType,

        Long fileSize

) {
}