package validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Checks {@link ValidPassport}: the passport number must match the format
 * of the country given in the same request.
 */
public class PassportValidator implements ConstraintValidator<ValidPassport, ClientRequest> {

    /** A passport number format and a human-readable example for error messages. */
    private record PassportFormat(Pattern pattern, String example) {
    }

    private static final Map<String, PassportFormat> FORMATS = Map.of(
            // Booklet: two Cyrillic letters and six digits; ID card: nine digits
            "UA", new PassportFormat(Pattern.compile("^([А-ЯҐЄІЇ]{2}\\d{6}|\\d{9})$"), "АА123456 or 123456789"),
            "PL", new PassportFormat(Pattern.compile("^[A-Z]{2}\\d{7}$"), "AB1234567")
    );

    @Override
    public boolean isValid(ClientRequest request, ConstraintValidatorContext context) {
        // Missing values are reported by the component-level constraints;
        // reporting them here as well would duplicate the errors
        if (request == null || isBlank(request.passportNumber()) || request.country() == null) {
            return true;
        }

        // Unknown country: the format can't be verified, so the number is accepted
        PassportFormat format = FORMATS.get(request.country());
        if (format == null || format.pattern().matcher(request.passportNumber()).matches()) {
            return true;
        }

        // Attach the error to the passportNumber field instead of the whole object.
        // The template contains only trusted text: the example comes from FORMATS and
        // the country is one of its keys. Raw user input must never be concatenated
        // into a template, because it would be interpreted as message parameters.
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        "Passport number must be in the format " + format.example()
                                + " for country " + request.country())
                .addPropertyNode("passportNumber")
                .addConstraintViolation();
        return false;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}