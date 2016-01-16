package se.leafcoders.rosette.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Payload;
import se.leafcoders.rosette.model.BaseModel;

@Documented
@Target( { FIELD } )
@Retention(RUNTIME)
public @interface CheckReference {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    boolean allowNull() default false;
    
    Class<?> model() default BaseModel.class;

    String dbKey() default "";
}
