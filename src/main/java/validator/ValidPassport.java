package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint: the passport number must match the format used in the
 * client's country. It needs two components at once, so it is placed on the
 * record rather than on a single component.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PassportValidator.class)
@Documented
public @interface ValidPassport {

    String message() default "Passport number doesn't match the format for the country";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}