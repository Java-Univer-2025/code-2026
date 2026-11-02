package stream;

import genericrepo.Client;
import lec5.Room;
import lec5.RoomType;

import java.util.List;

/**
 * Shows the stream and comparator queries of RoomRepository and ClientRepository.
 */
public class Demo {

    public static void main(String[] args) {
        RoomRepository rooms = new RoomRepository();
        rooms.addAll(List.of(
                new Room("101", RoomType.SINGLE, 800),
                new Room("102", RoomType.SINGLE, 950),
                new Room("201", RoomType.DOUBLE, 1200),
                new Room("202", RoomType.DOUBLE, 1200),
                new Room("301", RoomType.DELUXE, 3100),
                new Room("302", RoomType.DELUXE, 2500)
        ));
        rooms.addIfAbsent(new Room("101", RoomType.DELUXE, 5000)); // duplicate number

        System.out.println("\n=== Rooms ===");
        print("By price (natural order)", rooms.findAllSortedByPrice());
        print("By type, then by price", rooms.findAllSorted(RoomRepository.BY_TYPE_THEN_PRICE));
        print("Most expensive first", rooms.findAllSorted(RoomRepository.BY_PRICE_DESC));
        print("DOUBLE", rooms.findByType(RoomType.DOUBLE));
        print("Price from 900 to 2500", rooms.findByPriceRange(900, 2500));
        print("Three cheapest", rooms.findCheapest(3));
        System.out.println("Cheapest: " + rooms.findCheapest()
                .map(Room::number)
                .orElse("no rooms"));
        System.out.println("Count by type: " + rooms.countByType());
        System.out.println("Average price by type: " + rooms.averagePriceByType());
        System.out.println("Most expensive by type: " + rooms.findMostExpensiveByType());
        System.out.printf("Average price: %.2f%n", rooms.averagePrice().orElse(0));
        System.out.println("3 nights in 201: " + rooms.priceForStay("201", 3)
                .map(price -> price + " UAH")
                .orElse("room not found"));
        System.out.println("3 nights in 999: " + rooms.priceForStay("999", 3)
                .map(price -> price + " UAH")
                .orElse("room not found"));

        ClientRepository clients = new ClientRepository();
        clients.addAll(List.of(
                new Client("AA123456", "Ivan Petrenko", "ivan@gmail.com"),
                new Client("BB654321", "Yana Bondar", "yana@ukr.net"),
                new Client("CC111222", "Anna Koval", "anna@Gmail.com"),
                new Client("DD333444", "Yevhen Moroz", "yevhen@ukr.net"),
                new Client("EE555666", "Oleh Ivanenko", "oleh@outlook.com")
        ));

        System.out.println("\n=== Clients ===");
        print("By name", clients.findAllSortedByName());
        print("Name contains \"ivan\"", clients.findByNameContaining("ivan"));
        print("Domain gmail.com", clients.findByEmailDomain("gmail.com"));
        System.out.println("By email ANNA@gmail.com: " + clients.findByEmail("ANNA@gmail.com")
                .map(Client::getName)
                .orElse("not found"));
        System.out.println("Has yana@ukr.net: " + clients.existsByEmail("yana@ukr.net"));
        System.out.println("Grouped by domain:");
        clients.groupByEmailDomain().forEach((domain, group) ->
                System.out.println("  " + domain + " -> " + group.stream().map(Client::getName).toList()));
        System.out.println("All names: " + clients.namesAsText());
    }

    private static <T> void print(String title, List<T> items) {
        System.out.println(title + ":");
        items.forEach(item -> System.out.println("  " + item));
    }
}