package genericrepo;

import lec5.Room;
import lec5.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GenericRepositoryTest {

    private GenericRepository<Room> rooms;
    private Room r01;

    @BeforeEach
    void setUp() {
        rooms = new GenericRepository<>(Room.class, Room::number);
        r01 = new Room("R01", RoomType.SINGLE, 50.0);
    }

    @Test
    @DisplayName("add + findByIdentity finds the added element")
    void addThenFind() {
        rooms.add(r01);
        assertSame(r01, rooms.findByIdentity("R01").orElseThrow());
    }

    @Test
    @DisplayName("findByIdentity returns an empty Optional for a missing identity")
    void findMissing() {
        assertTrue(rooms.findByIdentity("R99").isEmpty());
    }

    @Test
    @DisplayName("add with an existing identity throws DuplicateIdentityException")
    void duplicateAddThrows() {
        rooms.add(r01);
        Room sameNumber = new Room("R01", RoomType.DOUBLE, 90.0);

        var e = assertThrows(DuplicateIdentityException.class, () -> rooms.add(sameNumber));
        assertTrue(e.getMessage().contains("R01"));
        assertTrue(e.getMessage().contains("Room"));
    }

    @Test
    @DisplayName("addIfAbsent returns false and does not change size()")
    void addIfAbsentOnDuplicate() {
        rooms.add(r01);
        assertFalse(rooms.addIfAbsent(r01));
        assertEquals(1, rooms.size());
    }

    @Test
    @DisplayName("removeByIdentity removes an existing element")
    void removeExisting() {
        rooms.add(r01);
        assertTrue(rooms.removeByIdentity("R01"));
        assertEquals(0, rooms.size());
    }

    @Test
    @DisplayName("removeByIdentity returns false for a missing element")
    void removeMissing() {
        rooms.add(r01);
        assertFalse(rooms.removeByIdentity("R99"));
        assertEquals(1, rooms.size());
    }

    @Test
    @DisplayName("changing the result of findAll() does not change the repository")
    void findAllIsUnmodifiable() {
        rooms.add(r01);
        List<Room> all = rooms.findAll();

        assertThrows(UnsupportedOperationException.class,
                () -> all.add(new Room("R02", RoomType.DOUBLE, 70.0)));
        assertEquals(1, rooms.size());
    }

    @Test
    @DisplayName("the same repository class works with a different type")
    void worksWithClient() {
        GenericRepository<Client> clients =
                new GenericRepository<>(Client.class, c -> c.getPassportNumber());
        Client client = new Client("AB123456", "Олена Коваль", "olena@mail.com");

        clients.add(client);
        assertSame(client, clients.findByIdentity("AB123456").orElseThrow());
    }

    @Test
    @DisplayName("add(null) and a blank identity throw IllegalArgumentException")
    void invalidElementsRejected() {
        assertThrows(IllegalArgumentException.class, () -> rooms.add(null));

        GenericRepository<Room> blankIds = new GenericRepository<>(Room.class, room -> " ");
        assertThrows(IllegalArgumentException.class, () -> blankIds.add(r01));
    }
}