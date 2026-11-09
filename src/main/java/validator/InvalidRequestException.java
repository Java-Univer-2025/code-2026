package validator;

import java.util.List;

/** Thrown when a request fails validation; carries the full list of field errors. */
public class InvalidRequestException extends RuntimeException {

    private final List<RequestValidator.FieldError> errors;

    public InvalidRequestException(List<RequestValidator.FieldError> errors) {
        super("Validation failed:" + System.lineSeparator() + RequestValidator.describe(errors));
        this.errors = List.copyOf(errors);
    }

    public List<RequestValidator.FieldError> getErrors() {
        return errors;
    }
}