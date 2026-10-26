package lec5;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static {
        // One-line log format: time, level, logger name, message.
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tT %4$-7s [%3$s] %5$s%n");
    }

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        HotelService hotel = new HotelService(createRepository(args));

        hotel.initIfEmpty();
        LOG.log(Level.INFO, "Кімнат у готелі: {0}", hotel.roomCount());
        LOG.log(Level.INFO, "Двомісні: {0}", hotel.roomsOfType(RoomType.DOUBLE));


        logStayPrice(hotel, "102", 3);
        logStayPrice(hotel, "999", 2);
    }

    /**
     * The only place that knows about concrete implementations.
     * With a file path argument data survives restarts; without it everything lives in memory.
     */
    private static RoomRepository createRepository(String[] args) {
        if (args.length > 0) {
            LOG.log(Level.INFO, "Сховище: файл {0}", args[0]);
            return new FileRoomRepository(Path.of(args[0]));
        }
        LOG.info("Сховище: пам'ять (дані не зберігаються між запусками)");
        return new InMemoryRoomRepository();
    }

    private static void logStayPrice(HotelService hotel, String number, int nights) {
        try {
            double price = hotel.stayPrice(number, nights);
            LOG.log(Level.INFO, "Кімната {0}, ночей: {1} — {2,number,0.00} грн",
                    new Object[]{number, nights, price});
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Кімнату {0} не знайдено", number);
        }
    }
}