package stream;

import genericrepo.GenericRepository;
import lec5.Room;
import lec5.RoomType;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Repository of hotel rooms with stream-based queries.
 *
 * Storage and uniqueness come from GenericRepository; this class fixes T = Room,
 * passes the Class token and the identity extractor, and adds read-only queries.
 * Every query works on findAll(), an unmodifiable snapshot, so a stream never
 * sees the repository change in the middle of processing.
 */
public class RoomRepository extends GenericRepository<Room> {

    /** Type first (enum declaration order), then price, then number as a tie-breaker. */
    public static final Comparator<Room> BY_TYPE_THEN_PRICE =
            Comparator.comparing(Room::type)
                    .thenComparingDouble(Room::pricePerNight)
                    .thenComparing(Room::number);

    /** Most expensive first; the number keeps the order deterministic for equal prices. */
    public static final Comparator<Room> BY_PRICE_DESC =
            Comparator.comparingDouble(Room::pricePerNight).reversed()
                    .thenComparing(Room::number);

    /** Creates an empty repository; rooms are identified by number. */
    public RoomRepository() {
        super(Room.class, Room::number);
    }

    /**
     * Returns rooms of the given type.
     *
     * @param type room type to look for
     * @return rooms of this type in insertion order; an empty list if there are none
     */
    public List<Room> findByType(RoomType type) {
        return findAll().stream()
                .filter(room -> room.type() == type)
                .toList();
    }

    /**
     * Returns rooms whose price per night is within the range, inclusive.
     *
     * @param min lowest price, inclusive
     * @param max highest price, inclusive
     * @return matching rooms, cheapest first; an empty list if none match
     * @throws IllegalArgumentException if {@code min > max}
     */
    public List<Room> findByPriceRange(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException(
                    "Minimum price is greater than maximum: " + min + " > " + max);
        }
        return findAll().stream()
                .filter(room -> room.pricePerNight() >= min && room.pricePerNight() <= max)
                .sorted() // natural order from Room.compareTo(): by price
                .toList();
    }

    /**
     * Returns all rooms in natural order (by price).
     * Rooms with equal prices keep insertion order: the sort is stable.
     *
     * @return unmodifiable list of rooms, cheapest first
     */
    public List<Room> findAllSortedByPrice() {
        return findAllSorted(Comparator.naturalOrder());
    }

    /**
     * Returns the cheapest room.
     *
     * @return the cheapest room, or an empty Optional if the repository is empty
     */
    public Optional<Room> findCheapest() {
        return findAll().stream()
                .min(Comparator.comparingDouble(Room::pricePerNight));
    }

    /**
     * Returns the {@code n} cheapest rooms.
     *
     * @param n maximum number of rooms to return
     * @return up to {@code n} rooms, cheapest first; fewer if the repository holds fewer rooms
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public List<Room> findCheapest(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Count must not be negative: " + n);
        }
        return findAll().stream()
                .sorted()
                .limit(n)
                .toList();
    }

    /**
     * Counts rooms of each type.
     *
     * @return map from type to the number of rooms, in enum declaration order;
     *         types without rooms are absent
     */
    public Map<RoomType, Long> countByType() {
        return findAll().stream()
                .collect(Collectors.groupingBy(
                        Room::type,
                        () -> new EnumMap<>(RoomType.class),
                        Collectors.counting()));
    }

    /**
     * Computes the average price per night for each type.
     *
     * @return map from type to the average price, in enum declaration order;
     *         types without rooms are absent
     */
    public Map<RoomType, Double> averagePriceByType() {
        return findAll().stream()
                .collect(Collectors.groupingBy(
                        Room::type,
                        () -> new EnumMap<>(RoomType.class),
                        Collectors.averagingDouble(Room::pricePerNight)));
    }

    /**
     * Finds the most expensive room of each type.
     * toMap() calls the merge function when two rooms share a type (key collision);
     * BinaryOperator.maxBy keeps the more expensive one.
     *
     * @return map from type to its most expensive room, in enum declaration order;
     *         types without rooms are absent
     */
    public Map<RoomType, Room> findMostExpensiveByType() {
        return findAll().stream()
                .collect(Collectors.toMap(
                        Room::type,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingDouble(Room::pricePerNight)),
                        () -> new EnumMap<>(RoomType.class)));
    }

    /**
     * Computes the average price per night over all rooms.
     *
     * @return the average price, or an empty OptionalDouble if the repository is empty
     */
    public OptionalDouble averagePrice() {
        return findAll().stream()
                .mapToDouble(Room::pricePerNight)
                .average();
    }

    /**
     * Computes the price of a stay in the given room.
     *
     * @param number room number
     * @param nights number of nights
     * @return the total price, or an empty Optional if there is no such room
     * @throws IllegalArgumentException if the room exists and {@code nights} is not positive
     */
    public Optional<Double> priceForStay(String number, int nights) {
        return findByIdentity(number)
                .map(room -> room.priceFor(nights));
    }
}