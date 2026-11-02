package stream;

import genericrepo.Client;
import genericrepo.GenericRepository;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Repository of hotel clients with stream-based queries.
 *
 * Client is mutable (name and e-mail can change), so queries read the current
 * values at call time; only the passport number, the identity, never changes.
 */
public class ClientRepository extends GenericRepository<Client> {

    // String.CASE_INSENSITIVE_ORDER compares Unicode code points, and Ukrainian
    // Є, І, Ї, Ґ lie outside the main Cyrillic range: "Іван" would sort after "Яна".
    // Collator applies the alphabet rules of the given locale.
    private static final Collator UKRAINIAN = Collator.getInstance(Locale.forLanguageTag("uk"));

    /** Name in Ukrainian alphabetical order; the passport number breaks ties between namesakes. */
    public static final Comparator<Client> BY_NAME =
            Comparator.comparing(Client::getName, UKRAINIAN)
                    .thenComparing(Client::getPassportNumber);

    /** Creates an empty repository; clients are identified by passport number. */
    public ClientRepository() {
        super(Client.class, Client::getPassportNumber);
    }

    /**
     * Returns clients whose name contains the given text, ignoring case.
     *
     * @param part text to search for; surrounding spaces are ignored
     * @return matching clients sorted by name; an empty list if none match
     * @throws NullPointerException if {@code part} is null
     */
    public List<Client> findByNameContaining(String part) {
        String needle = normalize(part);
        return findAll().stream()
                .filter(client -> normalize(client.getName()).contains(needle))
                .sorted(BY_NAME)
                .toList();
    }

    /**
     * Returns the client with the given e-mail, ignoring case and surrounding spaces.
     *
     * @param email e-mail to look for
     * @return the client, or an empty Optional if there is no client with this e-mail
     * @throws NullPointerException if {@code email} is null
     */
    public Optional<Client> findByEmail(String email) {
        String wanted = normalize(email);
        return findAll().stream()
                .filter(client -> normalize(client.getEmail()).equals(wanted))
                .findFirst();
    }

    /**
     * Checks whether a client with the given e-mail exists, ignoring case and surrounding spaces.
     * anyMatch() stops at the first match, without scanning the rest.
     *
     * @param email e-mail to look for
     * @return {@code true} if such a client exists
     * @throws NullPointerException if {@code email} is null
     */
    public boolean existsByEmail(String email) {
        String wanted = normalize(email);
        return findAll().stream()
                .anyMatch(client -> normalize(client.getEmail()).equals(wanted));
    }

    /**
     * Returns clients whose e-mail belongs to the given domain, ignoring case.
     *
     * @param domain domain without '@', e.g. {@code "gmail.com"}
     * @return matching clients sorted by name; an empty list if none match
     * @throws NullPointerException if {@code domain} is null
     */
    public List<Client> findByEmailDomain(String domain) {
        String wanted = normalize(domain);
        return findAll().stream()
                .filter(client -> domainOf(client.getEmail()).equals(wanted))
                .sorted(BY_NAME)
                .toList();
    }

    /**
     * Returns all clients sorted by name.
     *
     * @return unmodifiable list of clients ordered by {@link #BY_NAME}
     */
    public List<Client> findAllSortedByName() {
        return findAllSorted(BY_NAME);
    }

    /**
     * Groups clients by e-mail domain.
     * TreeMap sorts the domains; sorting before groupingBy() keeps each group sorted by name,
     * because a sequential stream adds elements to the groups in encounter order.
     *
     * @return map from lower-case domain to the clients of that domain, sorted by name;
     *         an empty map if the repository is empty
     */
    public Map<String, List<Client>> groupByEmailDomain() {
        return findAll().stream()
                .sorted(BY_NAME)
                .collect(Collectors.groupingBy(
                        client -> domainOf(client.getEmail()),
                        TreeMap::new,
                        Collectors.toList()));
    }

    /**
     * Returns all client names in alphabetical order as one line.
     *
     * @return names separated by ", ", e.g. {@code "Anna Koval, Ivan Petrenko"};
     *         an empty string if the repository is empty
     */
    public String namesAsText() {
        return findAll().stream()
                .map(Client::getName)
                .sorted(UKRAINIAN)
                .collect(Collectors.joining(", "));
    }

    // Locale.ROOT: toLowerCase() without a locale depends on the system settings
    // (the classic example is the Turkish dotless i).
    private static String normalize(String value) {
        Objects.requireNonNull(value, "Search text must not be null");
        return value.trim().toLowerCase(Locale.ROOT);
    }

    // Client guarantees that the e-mail contains '@'.
    private static String domainOf(String email) {
        return normalize(email.substring(email.lastIndexOf('@') + 1));
    }
}