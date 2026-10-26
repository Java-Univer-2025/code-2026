package lec_interface;

import lec5.Room;
import lec5.RoomType;

import java.util.*;

public class ComparableConsistencyDemo {

    public static void main(String[] args) {
        Room r101 = new Room("101", RoomType.SINGLE, 1500);
        Room r102 = new Room("102", RoomType.SINGLE, 1500);
        Room r201 = new Room("201", RoomType.DOUBLE, 2500);

        // 1. Set: TreeSet silently drops room 102
        Set<Room> hashSet = new HashSet<>(List.of(r101, r102, r201));
        Set<Room> treeSet = new TreeSet<>(List.of(r101, r102, r201));
        System.out.println("HashSet: " + hashSet.size() + " кімнати");
        System.out.println("TreeSet: " + treeSet.size() + " кімнати");

        // 2. contains(): TreeSet "finds" a room that was never added
        Room suite = new Room("999", RoomType.DELUXE, 1500);
        System.out.println("HashSet містить 999? " + hashSet.contains(suite));
        System.out.println("TreeSet містить 999? " + treeSet.contains(suite));

        // 3. Map: the second booking overwrites the first one
        Map<Room, String> bookings = new TreeMap<>();
        bookings.put(r101, "Іваненко");
        bookings.put(r102, "Петренко");
        System.out.println(bookings);
    }
}