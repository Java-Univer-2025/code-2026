package validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Runs Bean Validation on request objects and turns constraint violations
 * into a readable, stably ordered list of field errors.
 * <p>
 * Creating a {@link ValidatorFactory} is expensive, so one instance of this
 * class should be created and reused, then closed when no longer needed.
 */
public final class RequestValidator implements AutoCloseable {

    /** Field name for errors that belong to the whole object rather than one field. */
    private static final String OBJECT_LEVEL = "(request)";

    private final ValidatorFactory factory;
    private final Validator validator;

    public RequestValidator() {
        this.factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * A single validation error bound to a field. Serializable because it is
     * carried by {@link InvalidRequestException}, and exceptions are serializable.
     */
    public record FieldError(String field, String message) implements Serializable {

        @Override
        public String toString() {
            return field + ": " + message;
        }
    }

    /**
     * Validates the request and returns all errors sorted by field name.
     * An empty list means the request is valid.
     */
    public <T> List<FieldError> validate(T request) {
        Objects.requireNonNull(request, "Request must not be null");

        // Bean Validation does not guarantee the order of violations,
        // so the result is sorted to keep the output stable between runs
        return validator.validate(request).stream()
                .map(RequestValidator::toFieldError)
                .sorted(Comparator.comparing(FieldError::field).thenComparing(FieldError::message))
                .toList();
    }

    /**
     * Validates the request and returns it unchanged if it is valid.
     *
     * @throws InvalidRequestException if there is at least one error
     */
    public <T> T validateOrThrow(T request) {
        List<FieldError> errors = validate(request);
        if (!errors.isEmpty()) {
            throw new InvalidRequestException(errors);
        }
        return request;
    }

    /** Formats errors as a multi-line report, one error per line. */
    public static String describe(List<FieldError> errors) {
        return errors.stream()
                .map(error -> "  - " + error)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public void close() {
        factory.close();
    }

    private static FieldError toFieldError(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        return new FieldError(path.isEmpty() ? OBJECT_LEVEL : path, violation.getMessage());
    }
}