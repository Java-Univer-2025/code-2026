package lec5;

import java.util.Set;
import java.util.TreeSet;

public class CollectionsMain {
    public static void main(String[] args) {
        // Raw type Set (no <Room>): the compiler can't check element types,
// so rooms.add("kkk") below compiles. With Set<Room> it would be a compile error.
        Set rooms = new TreeSet<>();

        Room r1 = new Room("R01", RoomType.DOUBLE, 1.9);
        Room r2 = new Room("R01", RoomType.SINGLE, 8.9);

// true: same number, so the rooms are equal according to equals()
        System.out.println(r1.equals(r2));

// Added. Even the first add() calls compareTo() (r1.compareTo(r1)),
// because TreeSet checks right away that the element is Comparable.
        rooms.add(r1);

// Also added: r2.compareTo(r1) != 0 (8.9 vs 1.9), so TreeSet sees a NEW element,
// even though r1.equals(r2) is true. A HashSet would keep only one of them.
        rooms.add(r2);

// Opposite case: different number, same price as r1, so compareTo() == 0.
// TreeSet treats it as a duplicate, and add() returns false.
        Room r3 = new Room("R02", RoomType.SINGLE, 1.9);
        System.out.println(rooms.add(r3)); // false

// Fails at runtime with ClassCastException: TreeSet compares "kkk" with a Room
// using String.compareTo(), which cannot cast a Room to String.
        try {
            rooms.add("kkk");
        } catch (ClassCastException e) {
            System.out.println("Cannot add: " + e.getMessage());
        }

// Iteration order is the compareTo() order, so rooms are sorted by price ascending:
// R01 (1.9), then R01 (8.9)
        System.out.println(rooms);
    }
}
