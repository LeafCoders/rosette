package se.leafcoders.rosette;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Configuration
@Validated
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "rosette")
public class RosetteSettings {

    private @Value("${info.app.version:unknown}") String appVersion;

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
}
