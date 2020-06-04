package se.leafcoders.rosette.endpoint.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;

@Data
public class UserOut {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastLoginTime;
}
