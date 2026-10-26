package genericrepo;

/**
 * Tells the repository how to get the unique field of an object.
 * Implemented with lambdas, no separate classes needed.
 */
@FunctionalInterface
public interface IdentityExtractor<T> {
    String identityOf(T item);
}
