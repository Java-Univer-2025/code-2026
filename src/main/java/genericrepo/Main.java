package genericrepo;

import lec5.Room;
import lec5.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // A method reference is a short form of the lambda room -> room.number()
        GenericRepository<Room> rooms = new GenericRepository<>(Room.class, Room::number);
        GenericRepository<Client> clients =
                new GenericRepository<>(Client.class, client -> client.getPassportNumber());

        System.out.println("=== Наповнення з CSV ===");
        loadRooms(Path.of("rooms.csv"), rooms);
        System.out.println("Номерів у репозиторії: " + rooms.size());

        System.out.println("\n=== Клієнти ===");
        clients.add(new Client("AB123456", "Олена Коваль", "olena@mail.com"));
        clients.add(new Client("CD654321", "Петро Шевченко", "petro@mail.com"));

        System.out.println("\n=== Пошук за identity ===");
        find(rooms, "R01");
        find(rooms, "R99");
        find(clients, "AB123456");
        find(clients, "XX000000");

        System.out.println("\n=== Дублікат: add проти addIfAbsent ===");
        Client duplicate = new Client("AB123456", "Інша Особа", "other@mail.com");
        try {
            clients.add(duplicate);
        } catch (DuplicateIdentityException e) {
            // Handled here, so WARN, not ERROR
            log.warn("add відхилено: {}", e.getMessage());
        }
        System.out.println("addIfAbsent повернув: " + clients.addIfAbsent(duplicate));

        // Changing a mutable field after adding is safe: the identity (passport) is final
        clients.findByIdentity("CD654321").ifPresent(c -> c.setEmail("petro.new@mail.com"));
        clients.findAll().forEach(System.out::println);

        System.out.println("\n=== Видалення ===");
        System.out.println("R04 видалено: " + rooms.removeByIdentity("R04"));
        System.out.println("R99 видалено: " + rooms.removeByIdentity("R99"));

        System.out.println("\n=== Усі номери ===");
        rooms.findAll().forEach(System.out::println);

        System.out.println("\n=== ERROR: файл не знайдено ===");
        try {
            loadRooms(Path.of("missing.csv"), rooms);
        } catch (UncheckedIOException e) {
            // Already logged as ERROR inside loadRooms, so not logged again here
            System.out.println("Завантаження перервано: " + e.getCause().getMessage());
        }
    }

    private static <T> void find(GenericRepository<T> repo, String identity) {
        repo.findByIdentity(identity).ifPresentOrElse(
                item -> log.info("[{}] знайдено: {}", repo.getType().getSimpleName(), item),
                () -> log.warn("[{}] не знайдено: {}", repo.getType().getSimpleName(), identity));
    }

    /** Reads "number,type,pricePerNight" lines; a row with an existing number is skipped. */
    private static void loadRooms(Path file, GenericRepository<Room> repo) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            // The exception propagates further, so ERROR, and it is logged only here
            log.error("Не вдалося прочитати файл {}", file, e);
            throw new UncheckedIOException(e);
        }
        for (String line : lines.subList(1, lines.size())) {   // skip header
            String[] parts = line.split(",");
            Room room = new Room(parts[0].trim(),
                    RoomType.valueOf(parts[1].trim()),
                    Double.parseDouble(parts[2].trim()));
            try {
                repo.add(room);
            } catch (DuplicateIdentityException e) {
                log.warn("Рядок CSV пропущено: {}", e.getMessage());
            }
        }
    }
}
