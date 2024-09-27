package jsfas.common.exception;

import java.util.Set;

import com.networknt.schema.ValidationMessage;

public class JsonValidationFailedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3850116318843952842L;
	
	private final Set<ValidationMessage> validationMessages;

    public JsonValidationFailedException(Set<ValidationMessage> validationMessages) {
        super("Json validation failed: " + validationMessages);
        this.validationMessages = validationMessages;
    }

    public Set<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
}
