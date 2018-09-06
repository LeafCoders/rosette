package se.leafcoders.rosette;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "rosette")
public class RosetteSettings {

    private String baseUrl;

    private String apiVersion;

    private String jwtSecretToken;

    private String defaultMailFrom;

    private String adminMailTo;

    private String filesPath;
    private Long fileClientCacheMaxAge;

    private String cordateUrl;

    // Getters and setters


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getJwtSecretToken() {
        return jwtSecretToken;
    }

    public void setJwtSecretToken(String jwtSecretToken) {
        this.jwtSecretToken = jwtSecretToken;
    }

    public String getDefaultMailFrom() {
        return defaultMailFrom;
    }

    public void setDefaultMailFrom(String defaultMailFrom) {
        this.defaultMailFrom = defaultMailFrom;
    }

    public String getAdminMailTo() {
        return adminMailTo;
    }

    public void setAdminMailTo(String adminMailTo) {
        this.adminMailTo = adminMailTo;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public Long getFileClientCacheMaxAge() {
        return fileClientCacheMaxAge;
    }

    public void setFileClientCacheMaxAge(Long fileClientCacheMaxAge) {
        this.fileClientCacheMaxAge = fileClientCacheMaxAge;
    }

    public String getCordateUrl() {
        return cordateUrl;
    }

    public void setCordateUrl(String cordateUrl) {
        this.cordateUrl = cordateUrl;
    }
}
