package se.kth.iv1350.repairbike.integration;

/**
 * Contains information about one specific customer and that customer's bike.
 * Instances are immutable.
 */
public final class CustomerDTO {
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final BikeDTO bike;

    /**
     * Creates a new instance representing a particular customer.
     *
     * @param name        The customer's name.
     * @param phoneNumber The customer's phone number, used as identifier.
     * @param email       The customer's email address.
     * @param bike        The customer's bike.
     */
    public CustomerDTO(String name, String phoneNumber, String email, BikeDTO bike) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bike = bike;
    }

    /**
     * @return The customer's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The customer's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return The customer's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return The customer's bike.
     */
    public BikeDTO getBike() {
        return bike;
    }

    /**
     * @return A human-readable description of the customer and the customer's bike.
     */
    @Override
    public String toString() {
        return "Customer[name=" + name
                + ", phone=" + phoneNumber
                + ", email=" + email
                + ", " + bike + "]";
    }
}
