package genericrepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores elements of any type, keyed by an identity string.
 * Uniqueness is enforced here, not in the entity classes:
 * Room and Client know nothing about duplicates.
 *
 * The element type is passed as a Class token: because of type erasure,
 * GenericRepository<Room> and GenericRepository<Client> are the same class at runtime,
 * so the repository cannot find out T on its own.
 *
 * @param <T> element type
 */
public class GenericRepository<T> {

    private static final Logger log = LoggerFactory.getLogger(GenericRepository.class);

    private final Class<T> type;
    private final IdentityExtractor<T> extractor;

    // LinkedHashMap: O(1) lookup like HashMap, and findAll() returns elements
    // in insertion order, so the output is predictable.
    // HashMap: order not guaranteed. TreeMap: sorted by identity, but O(log n).
    private final Map<String, T> items = new LinkedHashMap<>();

    public GenericRepository(Class<T> type, IdentityExtractor<T> extractor) {
        this.type = Objects.requireNonNull(type, "Тип не може бути null");
        this.extractor = Objects.requireNonNull(extractor, "IdentityExtractor не може бути null");
    }

    /** Adds an element; throws DuplicateIdentityException if its identity is already present. */
    public void add(T item) {
        String identity = put(item);
        log.info("[{}] додано: {}", type.getSimpleName(), identity);
    }

    /** Adds an element only if its identity is absent; never throws on a duplicate. */
    public boolean addIfAbsent(T item) {
        String identity = identityOf(item);
        if (items.putIfAbsent(identity, item) != null) {
            log.warn("[{}] дублікат відхилено: {}", type.getSimpleName(), identity);
            return false;
        }
        log.info("[{}] додано: {}", type.getSimpleName(), identity);
        return true;
    }

    /**
     * Adds all elements and logs one summary line instead of one line per element.
     * Not atomic: stops at the first duplicate, elements before it stay added.
     */
    public void addAll(Collection<T> newItems) {
        for (T item : newItems) {
            put(item);
        }
        log.info("[{}] додано елементів: {}", type.getSimpleName(), newItems.size());
    }

    public boolean removeByIdentity(String identity) {
        if (items.remove(identity) == null) {
            log.warn("[{}] не знайдено для видалення: {}", type.getSimpleName(), identity);
            return false;
        }
        log.info("[{}] видалено: {}", type.getSimpleName(), identity);
        return true;
    }

    // Not logged here: the caller logs "found" / "not found" (one event, one place),
    // and the benchmark calls this method hundreds of thousands of times.
    public Optional<T> findByIdentity(String identity) {
        return Optional.ofNullable(items.get(identity));
    }

    // Unmodifiable copy: add/remove on it throws UnsupportedOperationException.
    // Note: the elements are shared, so a mutable Client can still be changed through it.
    // That is why the identity must come from a field that never changes.
    public List<T> findAll() {
        return List.copyOf(items.values());
    }

    public int size() {
        return items.size();
    }

    public Class<T> getType() {
        return type;
    }

    // Validates and stores the element. A duplicate is not logged here:
    // the caller decides whether it is WARN (handled) or ERROR (propagated further).
    private String put(T item) {
        String identity = identityOf(item);
        if (items.containsKey(identity)) {
            throw new DuplicateIdentityException(identity, type.getSimpleName());
        }
        items.put(identity, item);
        return identity;
    }

    private String identityOf(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Елемент не може бути null");
        }
        String identity = extractor.identityOf(item);
        if (identity == null || identity.isBlank()) {
            throw new IllegalArgumentException("Порожня identity в елемента: " + item);
        }
        return identity;
    }
}