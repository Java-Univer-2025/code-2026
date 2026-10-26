package genericrepo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Compares lookup in the repository's Map with a linear search in a List.
 * The repository itself is not changed.
 */
public class SearchBenchmark {

    private static final int SIZE = 100_000;
    private static final int CALLS = 1_000;   // one lookup is too fast to time alone
    private static final int ROUNDS = 7;      // repeat each measurement, take the median

    // Results are accumulated here so the JIT cannot remove the "unused" searches
    private static long sink;

    public static void main(String[] args) {
        IdentityExtractor<Client> extractor = Client::getPassportNumber;
        GenericRepository<Client> repo = new GenericRepository<>(Client.class, extractor);

        Client[] generated = new Client[SIZE];
        for (int i = 0; i < SIZE; i++) {
            generated[i] = new Client("P" + i, "Client " + i, "client" + i + "@mail.com");
        }
        repo.addAll(Arrays.asList(generated));

        // Taken once, before measuring: inside the loop we would measure copying, not search
        List<Client> list = repo.findAll();

        String first = extractor.identityOf(list.get(0));
        String last = extractor.identityOf(list.get(list.size() - 1));
        String missing = "NO-SUCH-ID";

        // Warm-up: let the JIT compile both search paths before measuring
        for (int i = 0; i < 3; i++) {
            for (String id : List.of(first, last, missing)) {
                measure(() -> repo.findByIdentity(id));
                measure(() -> linearSearch(list, id, extractor));
            }
        }

        System.out.printf("%-12s %14s %14s%n", "Scenario", "Map, ns", "List, ns");
        report("first", first, repo, list, extractor);
        report("last", last, repo, list, extractor);
        report("missing", missing, repo, list, extractor);
        System.out.println("(found: " + sink + ")");
    }

    private static void report(String scenario, String id, GenericRepository<Client> repo,
                               List<Client> list, IdentityExtractor<Client> extractor) {
        double map = measure(() -> repo.findByIdentity(id));
        double linear = measure(() -> linearSearch(list, id, extractor));
        System.out.printf("%-12s %14.1f %14.1f%n", scenario, map, linear);
    }

    /** Median time of one search call, in nanoseconds. */
    private static double measure(Supplier<Optional<Client>> search) {
        long[] samples = new long[ROUNDS];
        for (int r = 0; r < ROUNDS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < CALLS; i++) {
                if (search.get().isPresent()) {
                    sink++;
                }
            }
            samples[r] = System.nanoTime() - start;
        }
        Arrays.sort(samples);
        return (double) samples[ROUNDS / 2] / CALLS;
    }

    private static <E> Optional<E> linearSearch(
            List<E> items, String identity, IdentityExtractor<E> extractor) {
        for (E item : items) {
            if (identity.equals(extractor.identityOf(item))) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}