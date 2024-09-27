package jsfas.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jsfas.common.annotation.constraints.Equals;

/**
 * Validator for Equals constraint
 * Asserts that the annotated string is equals to ONE of defined value(s)
 * <br>Skip if the annotated string is null
 *
 * @author iswill
 * @since 28/08/2017
 * @version 1.0a
 */
public class EqualsValidator implements ConstraintValidator<Equals, String> {

    private String[] values;

    @Override
    public void initialize(Equals constraintAnnotation) {
        this.values = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }
        
        for (String value : values) {
        	if (object.equals(value)) {
        		return true;
        	}
        }

        return false;
    }
}
