package se.leafcoders.rosette.endpoint.message;

import lombok.Data;

@Data
public class MessageOut {

    private Long id;
    private String key;
    private String language;
    private String message;
}
