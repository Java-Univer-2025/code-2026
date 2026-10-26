package lec5;

import java.util.List;

/**
 * Stores rooms, each identified by its number.
 */
public interface RoomRepository {

    /**
     * Adds the room.
     *
     * @throws IllegalArgumentException if the room is null
     * @throws IllegalStateException    if a room with the same number is already stored
     */
    void add(Room room);

    /**
     * Returns the room with the given number.
     *
     * @return the room, or null if nothing matches
     */
    Room findByNumber(String number);

    /**
     * Returns all stored rooms in insertion order.
     *
     * @return a copy; changes to it do not affect the repository.
     *         Empty repository returns an empty list, never null.
     */
    List<Room> findAll();
}