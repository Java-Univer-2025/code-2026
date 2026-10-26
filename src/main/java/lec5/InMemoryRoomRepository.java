package lec5;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, Room> rooms = new LinkedHashMap<>();

    @Override
    public void add(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room must not be null");
        }
        if (rooms.containsKey(room.number())) {
            throw new IllegalStateException("Room already stored: " + room.number());
        }
        rooms.put(room.number(), room);
    }

    @Override
    public Room findByNumber(String number) {
        return rooms.get(number);          // null when absent — as the contract says
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());   // a copy, insertion order preserved
    }
}
