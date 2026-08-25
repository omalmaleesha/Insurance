package com.example.Insurance.service.storage;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFile upload(
            MultipartFile file,
            String folderId
    );

    StorageFile download(
            String fileId
    );

    void delete(
            String fileId
    );
}