package se.kth.iv1350.repairbike.integration;

/**
 * Thrown when the integration layer cannot reach the underlying data store,
 * for example because the database server is down or a network error
 * prevented the call from completing. This is an unchecked exception, since
 * the calling layers cannot meaningfully recover from this condition during
 * the normal use case; the failure must be reported to the user and logged
 * for the developers, but the program cannot continue with the same business
 * action.
 *
 * <p>This exception is named after the error condition (a failure to call the
 * database), and is thrown at the abstraction level of the integration layer
 * so that callers in higher layers do not need to know about the underlying
 * cause (network, file system, etc.).</p>
 */
public class DatabaseFailureException extends RuntimeException {
    private final String failingIdentifier;

    /**
     * Creates a new instance for the case where the database call failed
     * while looking up a specific identifier.
     *
     * @param failingIdentifier The identifier whose lookup caused the
     *                          database call to fail.
     * @param cause             The underlying technical cause, or
     *                          {@code null} if no underlying exception is
     *                          available.
     */
    public DatabaseFailureException(String failingIdentifier, Throwable cause) {
        super("Could not reach the database while looking up identifier "
              + failingIdentifier + ".", cause);
        this.failingIdentifier = failingIdentifier;
    }

    /**
     * Creates a new instance for the case where the database call failed
     * while looking up a specific identifier and no underlying cause is
     * available.
     *
     * @param failingIdentifier The identifier whose lookup caused the
     *                          database call to fail.
     */
    public DatabaseFailureException(String failingIdentifier) {
        this(failingIdentifier, null);
    }

    /**
     * @return The identifier whose lookup caused the database call to fail.
     */
    public String getFailingIdentifier() {
        return failingIdentifier;
    }
}
