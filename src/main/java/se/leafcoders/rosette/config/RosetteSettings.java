package se.leafcoders.rosette.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "rosette")
public class RosetteSettings {
    private String baseUrl;

	private String apiVersion;

    private String jwtSecretToken;

    private String defaultMailFrom;

	private int uploadCacheMaxAge;


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

    public int getUploadCacheMaxAge() {
        return uploadCacheMaxAge;
    }

    public void setUploadCacheMaxAge(int uploadCacheMaxAge) {
        this.uploadCacheMaxAge = uploadCacheMaxAge;
    }
}