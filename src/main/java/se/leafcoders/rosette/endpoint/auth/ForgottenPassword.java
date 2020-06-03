package se.leafcoders.rosette.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.exception.ApiString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "forgottenpassword")
public class ForgottenPassword extends Persistable {

    private static final long serialVersionUID = 6959670561626507450L;

    @NotNull(message = ApiString.NOT_NULL)
    private Long userId;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String token;
}
