package lec5;

import java.util.ArrayList;
import java.util.List;

/**
 * Hotel business logic. Depends only on the RoomRepository interface,
 * never on a concrete storage.
 */
public class HotelService {

    private final RoomRepository rooms;

    public HotelService(RoomRepository rooms) {
        if (rooms == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.rooms = rooms;
    }

    /**
     * Fills the hotel with default rooms if it has none yet.
     *
     * @return true if rooms were added
     */
    public boolean initIfEmpty() {
        if (!rooms.findAll().isEmpty()) {
            return false;
        }
        rooms.add(new Room("101", RoomType.SINGLE, 50));
        rooms.add(new Room("102", RoomType.DOUBLE, 80));
        rooms.add(new Room("201", RoomType.DELUXE, 150));
        return true;
    }

    public int roomCount() {
        return rooms.findAll().size();
    }

    /**
     * Total price for staying in the room.
     *
     * @throws IllegalArgumentException if there is no room with this number
     */
    public double stayPrice(String number, int nights) {
        Room room = rooms.findByNumber(number);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + number);
        }
        return room.priceFor(nights);
    }

    public List<Room> roomsOfType(RoomType type) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms.findAll()) {
            if (room.type() == type) {
                result.add(room);
            }
        }
        return result;
    }
}