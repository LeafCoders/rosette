package se.leafcoders.rosette.endpoint.group;

import java.util.List;

import lombok.Data;
import se.leafcoders.rosette.endpoint.user.UserRefOut;

@Data
public class GroupOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private List<UserRefOut> users;
}
