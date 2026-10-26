package genericrepo;

/**
 * Thrown by {@link GenericRepository#add} when an element with the same identity
 * is already stored. Unchecked: extends IllegalStateException.
 */
public class DuplicateIdentityException extends IllegalStateException {

    public DuplicateIdentityException(String identity, String typeName) {
        super("Елемент з identity '" + identity + "' вже є в репозиторії типу '" + typeName + "'");
    }
}