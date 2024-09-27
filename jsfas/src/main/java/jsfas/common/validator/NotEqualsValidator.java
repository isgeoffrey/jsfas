package jsfas.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import jsfas.common.annotation.constraints.NotEquals;

/**
 * Validator for NotEquals constraint
 * <br>Asserts that the annotated string is not equals to ANY of defined value(s)
 * <br>Skip if the annotated string is null
 *
 * @author iswill
 * @since 28/08/2017
 * @version 1.0a
 */
public class NotEqualsValidator implements ConstraintValidator<NotEquals, String> {

    private String[] values;

    @Override
    public void initialize(NotEquals constraintAnnotation) {
        this.values = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
    	if (object == null) {
            return true;
        }
        
        for (String value : values) {
        	if (object.equals(value)) {
        		return false;
        	}
        }

        return true;
    }
}
