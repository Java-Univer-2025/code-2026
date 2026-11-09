package validator;

import genericrepo.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Input data for creating a {@link Client}.
 * <p>
 * Component-level constraints check each value on its own. {@link ValidPassport}
 * on the record is a cross-field check: the passport number must match the
 * format used in the client's country.
 */
@ValidPassport
public record ClientRequest(

        @NotBlank(message = "Passport number can't be empty")
        String passportNumber,

        // @NotNull instead of @NotBlank: an empty string already fails @Pattern,
        // so the user gets one error instead of two
        @NotNull(message = "Country is required")
        @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a two-letter code, e.g. UA")
        String country,

        @NotBlank(message = "Name can't be empty")
        @Size(max = 100, message = "Name can't be longer than 100 characters")
        String name,

        @NotBlank(message = "Email can't be empty")
        @Email(message = "Incorrect email")
        String email
) {

    /**
     * Converts the request into a domain object. Call only after successful validation.
     * The country is not part of {@link Client}: it is used for validation only.
     */
    public Client toClient() {
        return new Client(passportNumber, name, email);
    }
}