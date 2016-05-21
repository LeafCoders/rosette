package se.leafcoders.rosette.model;

import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;

@Document(collection = "forgottenPasswords")
public class ForgottenPassword extends IdBasedModel {

    @CreatedDate
    @JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
    @JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
    private Date createdTime;   

    @NotEmpty
    private String token;
    
    @NotEmpty
    private String userId;

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
    }

    // Getters and setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
