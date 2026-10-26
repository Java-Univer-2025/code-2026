package lec5;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores rooms in a CSV file, one room per line: number,type,pricePerNight
 */
public class FileRoomRepository implements RoomRepository {

    private static final String SEPARATOR = ",";

    private final Path file;

    public FileRoomRepository(Path file) {
        this.file = file;
    }

    @Override
    public void add(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room must not be null");
        }
        if (findByNumber(room.number()) != null) {
            throw new IllegalStateException("Room already exists: " + room.number());
        }
        String line = toCsv(room) + System.lineSeparator();
        try {
            Files.writeString(file, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write to " + file, e);
        }
    }

    @Override
    public Room findByNumber(String number) {
        for (Room room : findAll()) {
            if (room.number().equals(number)) {
                return room;
            }
        }
        return null;  // null when absent — as the contract says
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        if (Files.notExists(file)) {
            return rooms; // no file yet = empty repository, never null
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    rooms.add(fromCsv(line));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + file, e);
        }
        return rooms; // a fresh list on every call, so callers cannot affect the file
    }

    private static String toCsv(Room room) {
        return room.number() + SEPARATOR + room.type() + SEPARATOR + room.pricePerNight();
    }

    private static Room fromCsv(String line) {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 3) {
            throw new IllegalStateException("Malformed line: " + line);
        }
        return new Room(parts[0], RoomType.valueOf(parts[1]), Double.parseDouble(parts[2]));
    }
}
