package lec5;

import java.util.Objects;

/**
 * A hotel room. Identified by its number.
 */
public record Room(String number, RoomType type, double pricePerNight) implements Comparable<Room> {

    public Room {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Room number must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Room type must not be null");
        }
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price must be positive: " + pricePerNight);
        }
    }


    @Override
    public boolean equals(Object o) {
        // getClass() check (not instanceof): a subclass instance is never equal to a Room
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(number, room.number);
    }

    // Contract: equal objects must have equal hash codes,
// so hashCode() uses the same field as equals(), which is number.
    @Override
    public int hashCode() {
        return Objects.hashCode(number());
    }

    // Natural ordering: rooms are sorted by price per night.
// TreeSet uses ONLY this method, both to sort elements and to detect duplicates:
// compareTo() == 0 means "same element", so the new one is not added.
// WARNING: this ordering is inconsistent with equals():
//   same number, different price -> equals() is true, but compareTo() != 0
//   different number, same price -> equals() is false, but compareTo() == 0
// Double.compare() is used instead of (int)(a - b), because subtraction
// truncates small differences like 0.3 to 0 and mishandles NaN.
    @Override
    public int compareTo(Room other) {
        return Double.compare(pricePerNight, other.pricePerNight);
    }

    /** Total price for the given number of nights. */
    public double priceFor(int nights) {
        if (nights <= 0) {
            throw new IllegalArgumentException("Nights must be positive: " + nights);
        }
        return pricePerNight * nights;
    }
}
