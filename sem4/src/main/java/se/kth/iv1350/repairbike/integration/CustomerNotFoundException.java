package se.kth.iv1350.repairbike.integration;

/**
 * Thrown when a search for a customer by phone number cannot find a matching
 * customer in the {@link CustomerRegistry}. This is a checked exception, since
 * it represents a recoverable, predictable business condition (no such
 * customer in the registry) that the calling layers can and must handle
 * directly, for example by asking the receptionist for a different phone
 * number.
 */
public class CustomerNotFoundException extends Exception {
    private final String missingPhoneNumber;

    /**
     * Creates a new instance with a message identifying the phone number that
     * could not be found.
     *
     * @param missingPhoneNumber The phone number that was searched for but
     *                           that does not exist in the customer registry.
     */
    public CustomerNotFoundException(String missingPhoneNumber) {
        super("Could not find any customer with phone number "
              + missingPhoneNumber + ".");
        this.missingPhoneNumber = missingPhoneNumber;
    }

    /**
     * @return The phone number that was searched for but that does not exist
     *         in the customer registry.
     */
    public String getMissingPhoneNumber() {
        return missingPhoneNumber;
    }
}
