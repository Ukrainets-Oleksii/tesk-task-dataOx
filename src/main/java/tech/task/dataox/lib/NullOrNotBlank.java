package tech.task.dataox.lib;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tech.task.dataox.lib.impl.NullOrNotBlankValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "must be null or non-blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}