package genericrepo;


/**
 * A hotel client. Mutable, except for the passport number, which is the identity.
 * Has no equals/hashCode: the repository does not need them, it compares identities.
 */
public class Client {

    // final: the identity must not change after the client is added to a repository,
    // otherwise the Map key and the object's own passport number would disagree
    private final String passportNumber;
    private String name;
    private String email;

    public Client(String passportNumber, String name, String email) {
        this.passportNumber = requireNotBlank(passportNumber, "Passport number");
        this.name = requireNotBlank(name, "Name");
        this.email = requireEmail(email);
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNotBlank(name, "Name");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireEmail(email);
    }

    @Override
    public String toString() {
        return "Client[passportNumber=" + passportNumber + ", name=" + name + ", email=" + email + "]";
    }

    private static String requireNotBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " can't be empty");
        }
        return value;
    }

    private static String requireEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Incorrect email : " + email);
        }
        return email;
    }
}
