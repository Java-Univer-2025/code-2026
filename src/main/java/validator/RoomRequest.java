package validator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lec5.Room;
import lec5.RoomType;

/**
 * Input data for creating a {@link Room}.
 * <p>
 * The price is a {@code Double}, not a {@code double}: a request must be able to
 * tell "no price given" from "price is 0". With a primitive, a missing value
 * would silently become 0.0.
 */
public record RoomRequest(

        @NotBlank(message = "Room number can't be empty")
        String number,

        @NotNull(message = "Room type is required")
        RoomType type,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double pricePerNight
) {

    /** Converts the request into a domain object. Call only after successful validation. */
    public Room toRoom() {
        return new Room(number, type, pricePerNight);
    }
}