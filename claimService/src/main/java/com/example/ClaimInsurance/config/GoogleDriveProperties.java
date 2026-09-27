package com.example.ClaimInsurance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Getter
@Setter
@ConfigurationProperties(prefix = "google.drive")
public class GoogleDriveProperties {

    private OAuth oauth = new OAuth();

    private String rootFolderId;

    private String applicationName;

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public void setRootFolderId(String rootFolderId) {
        this.rootFolderId = rootFolderId;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public static class OAuth {

        private String credentialsPath;

        public String getCredentialsPath() {
            return credentialsPath;
        }

        public void setCredentialsPath(String credentialsPath) {
            this.credentialsPath = credentialsPath;
        }
    }
}