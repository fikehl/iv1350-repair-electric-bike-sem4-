package se.kth.iv1350.repairbike.integration;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all calls to the data store with customers and their bikes. In this
 * version of the program there is no real database, instead a hard-coded list
 * of customers is kept inside this class. The class would otherwise have called
 * an external customer registry to retrieve the data.
 *
 * <p>The class simulates two error conditions that can occur in the real
 * integration layer: searching for a phone number that has no customer in the
 * registry, and a complete failure to reach the database server.</p>
 */
public class CustomerRegistry {
    /**
     * A hard-coded phone number used to simulate that the database server is
     * unreachable. Calling {@link #findCustomer(String)} with this number
     * always throws a {@link DatabaseFailureException}.
     */
    public static final String DATABASE_FAILURE_PHONE_NUMBER = "0700000000";

    private final List<CustomerDTO> customers = new ArrayList<>();

    /**
     * Creates a new instance and populates the registry with hard-coded
     * customer data.
     */
    CustomerRegistry() {
        addHardcodedCustomers();
    }

    /**
     * Searches for the customer with the specified phone number.
     *
     * @param phoneNumber The phone number identifying the searched customer.
     * @return The customer matching the specified phone number. The return
     *         value is never {@code null}.
     * @throws CustomerNotFoundException If no customer in the registry has
     *                                   the specified phone number.
     * @throws DatabaseFailureException  If the database call could not be
     *                                   completed. This happens whenever
     *                                   {@code phoneNumber} equals
     *                                   {@link #DATABASE_FAILURE_PHONE_NUMBER},
     *                                   which simulates an unreachable
     *                                   database server.
     */
    public CustomerDTO findCustomer(String phoneNumber)
            throws CustomerNotFoundException {
        if (DATABASE_FAILURE_PHONE_NUMBER.equals(phoneNumber)) {
            throw new DatabaseFailureException(phoneNumber);
        }
        for (CustomerDTO customer : customers) {
            if (customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }
        throw new CustomerNotFoundException(phoneNumber);
    }

    private void addHardcodedCustomers() {
        customers.add(new CustomerDTO(
                "Anna Andersson", "0701112233", "anna@example.com",
                new BikeDTO("Crescent", "Elina E8", "CR-E8-00417")));
        customers.add(new CustomerDTO(
                "Bertil Bengtsson", "0734445566", "bertil@example.com",
                new BikeDTO("Cube", "Reaction Hybrid", "CB-RH-92133")));
        customers.add(new CustomerDTO(
                "Cecilia Carlsson", "0767778899", "cecilia@example.com",
                new BikeDTO("Trek", "Verve+ 2", "TR-V2-55021")));
    }
}
