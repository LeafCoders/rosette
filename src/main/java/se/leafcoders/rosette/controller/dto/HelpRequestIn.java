package se.leafcoders.rosette.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HelpRequestIn {

    private String fromName;
    private String fromEmail;
    private String subject;
    private String message;
}
