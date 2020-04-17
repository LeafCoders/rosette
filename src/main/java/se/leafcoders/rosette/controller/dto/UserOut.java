package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

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
