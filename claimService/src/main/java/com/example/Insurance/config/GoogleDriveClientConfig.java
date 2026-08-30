package com.example.Insurance.config;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class GoogleDriveClientConfig {

    private static final GsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private final GoogleDriveProperties properties;

    public GoogleDriveClientConfig(
            GoogleDriveProperties properties
    ) {
        this.properties = properties;
    }

    @Bean
    public Drive googleDriveClient() throws Exception {

        NetHttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        Path credentialsPath = Path.of(
                properties.getOauth()
                        .getCredentialsPath()
        );

        try (InputStream inputStream =
                     Files.newInputStream(credentialsPath)) {

            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(
                            JSON_FACTORY,
                            new InputStreamReader(inputStream)
                    );

            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            httpTransport,
                            JSON_FACTORY,
                            clientSecrets,
                            List.of(DriveScopes.DRIVE_FILE)
                    )
                            .setDataStoreFactory(
                                    new FileDataStoreFactory(
                                            new java.io.File(
                                                    "tokens"
                                            )
                                    )
                            )
                            .setAccessType("offline")
                            .build();

            LocalServerReceiver receiver =
                    new LocalServerReceiver.Builder()
                            .setPort(8888)
                            .build();

            var credential =
                    new AuthorizationCodeInstalledApp(
                            flow,
                            receiver
                    ).authorize("claim-service");

            return new Drive.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    credential
            )
                    .setApplicationName(
                            properties.getApplicationName()
                    )
                    .build();
        }
    }
}