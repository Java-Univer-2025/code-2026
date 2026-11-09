package validator;

import genericrepo.Client;
import lec5.Room;
import lec5.RoomType;

import java.util.List;

/**
 * Demonstrates request validation: standard constraints, the custom
 * {@link ValidPassport} cross-field constraint and error reporting.
 */
public class ValidationDemo {

    public static void main(String[] args) {
        try (RequestValidator validator = new RequestValidator()) {
            showClients(validator);
            showRooms(validator);
            showValidateOrThrow(validator);
        }
    }

    private static void showClients(RequestValidator validator) {
        printHeader("Clients");

        check(validator, "Valid Ukrainian client (ID card)",
                new ClientRequest("123456789", "UA", "Ivan Petrenko", "ivan@example.com"));

        check(validator, "Valid Polish client",
                new ClientRequest("AB1234567", "PL", "Anna Kowalska", "anna@example.pl"));

        check(validator, "Unknown country: the format can't be checked, so the number is accepted",
                new ClientRequest("C01X00T47", "DE", "Hans Muller", "hans@example.de"));

        check(validator, "Custom constraint: Polish-format number for a Ukrainian client",
                new ClientRequest("AB1234567", "UA", "Olena Shevchenko", "olena@example.com"));

        check(validator, "Several errors at once",
                new ClientRequest("", "Ukraine", " ", "not-an-email"));

        ClientRequest request = new ClientRequest("АА123456", "UA", "Petro Ivanenko", "petro@example.com");
        if (validator.validate(request).isEmpty()) {
            Client client = request.toClient();
            System.out.println("Converted to a domain object: " + client);
            System.out.println();
        }
    }

    private static void showRooms(RequestValidator validator) {
        printHeader("Rooms");

        // Any existing room type; can be replaced with a concrete constant
        RoomType someType = RoomType.values()[0];

        RoomRequest valid = new RoomRequest("101", someType, 1200.0);
        check(validator, "Valid room", valid);

        check(validator, "Missing type and price",
                new RoomRequest("102", null, null));

        check(validator, "Blank number and negative price",
                new RoomRequest("  ", someType, -500.0));

        if (validator.validate(valid).isEmpty()) {
            Room room = valid.toRoom();
            System.out.println("Converted to a domain object: " + room);
            System.out.println();
        }
    }

    private static void showValidateOrThrow(RequestValidator validator) {
        printHeader("validateOrThrow");

        try {
            ClientRequest request = validator.validateOrThrow(
                    new ClientRequest("12345", "UA", "Taras Bondar", "taras@example.com"));
            System.out.println("Saved: " + request.toClient());
        } catch (InvalidRequestException e) {
            System.out.println(e.getMessage());
            System.out.println("Number of errors: " + e.getErrors().size());
        }
    }

    private static void check(RequestValidator validator, String title, Object request) {
        List<RequestValidator.FieldError> errors = validator.validate(request);
        System.out.println(title);
        System.out.println(errors.isEmpty() ? "  OK" : RequestValidator.describe(errors));
        System.out.println();
    }

    private static void printHeader(String title) {
        System.out.println("=== " + title + " ===");
        System.out.println();
    }
}