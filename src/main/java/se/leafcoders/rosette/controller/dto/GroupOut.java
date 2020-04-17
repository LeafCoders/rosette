package se.leafcoders.rosette.controller.dto;

import java.util.List;

import lombok.Data;

@Data
public class GroupOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private List<UserRefOut> users;
}
