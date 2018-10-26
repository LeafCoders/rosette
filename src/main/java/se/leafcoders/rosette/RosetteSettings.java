package se.leafcoders.rosette;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "rosette")
public class RosetteSettings {

    @Pattern(regexp = "(http|https):\\/\\/.*\\/", message = "Must start with 'http://' or 'https://' and end with a '/'")
    private String baseUrl;

    private String jwtSecretToken;

    @Email
    private String defaultMailFrom;

    @Email
    private String adminMailTo;

    private String filesPath;
    private Long fileClientCacheMaxAge;

    @Pattern(regexp = "(http|https):\\/\\/.*\\/", message = "Must start with 'http://' or 'https://' and end with a '/'")
    private String cordateUrl;

    // Getters and setters


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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
