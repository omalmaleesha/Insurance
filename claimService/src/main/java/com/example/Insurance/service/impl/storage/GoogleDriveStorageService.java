package com.example.Insurance.service.impl.storage;

import com.example.Insurance.service.storage.FileStorageService;
import com.example.Insurance.service.storage.StoredFile;
import com.example.Insurance.service.storage.StorageFile;
import com.example.Insurance.utils.types.StorageProvider;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleDriveStorageService
        implements FileStorageService {

    private final Drive drive;

    @Override
    public StoredFile upload(
            MultipartFile multipartFile,
            String folderId
    ) {

        try {

            String originalFileName =
                    sanitizeFileName(
                            multipartFile.getOriginalFilename()
                    );

            String storedFileName =
                    UUID.randomUUID()
                            + getExtension(originalFileName);

            File metadata = new File();

            metadata.setName(storedFileName);

            metadata.setParents(
                    List.of(folderId)
            );

            InputStreamContent mediaContent =
                    new InputStreamContent(
                            multipartFile.getContentType(),
                            multipartFile.getInputStream()
                    );

            File uploadedFile =
                    drive.files()
                            .create(
                                    metadata,
                                    mediaContent
                            )
                            .setFields(
                                    "id,name,mimeType,size,parents"
                            )
                            .execute();

            return new StoredFile(

                    StorageProvider.GOOGLE_DRIVE,

                    uploadedFile.getId(),

                    folderId,

                    uploadedFile.getName(),

                    uploadedFile.getMimeType(),

                    uploadedFile.getSize()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload document to cloud storage",
                    e
            );
        }
    }


    @Override
    public StorageFile download(String fileId) {

        try {

            File metadata =
                    drive.files()
                            .get(fileId)
                            .setFields(
                                    "id,name,mimeType,size"
                            )
                            .execute();

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            drive.files()
                    .get(fileId)
                    .executeMediaAndDownloadTo(
                            outputStream
                    );

            return new StorageFile(

                    new ByteArrayInputStream(
                            outputStream.toByteArray()
                    ),

                    metadata.getName(),

                    metadata.getMimeType(),

                    metadata.getSize()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to download document from cloud storage",
                    e
            );
        }
    }


    @Override
    public void delete(String fileId) {

        try {

            drive.files()
                    .delete(fileId)
                    .execute();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete document from cloud storage",
                    e
            );
        }
    }


    private String sanitizeFileName(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return "unknown";
        }

        return fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }


    private String getExtension(String fileName) {

        int lastDot =
                fileName.lastIndexOf(".");

        if (lastDot == -1) {
            return "";
        }

        return fileName.substring(lastDot);
    }
}