package jsfas.common.annotation.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jsfas.common.validator.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Asserts that the annotated string is equals to ONE of defined value(s)
 * <br>Skip if the annotated string is null
 *
 * @author iswill
 * @since 28/08/2017
 * @version 1.0a
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EqualsValidator.class)
@Documented
public @interface Equals {

    String message() default "{validator.equals.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String[] value();
}