package se.kth.iv1350.repairbike.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CustomerRegistry}. The registry is populated with
 * hard-coded customers, so the tests rely on the same data being present.
 */
public class CustomerRegistryTest {
    private CustomerRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = RegistryCreator.getInstance().getCustomerRegistry();
    }

    @AfterEach
    public void tearDown() {
        registry = null;
    }

    @Test
    public void testFindExistingCustomerReturnsMatchingCustomer() throws CustomerNotFoundException {
        CustomerDTO found = registry.findCustomer("0701112233");
        assertNotNull(found,
                "findCustomer returned null for a known phone number.");
        assertEquals("0701112233", found.getPhoneNumber(),
                "findCustomer returned a customer with a different phone number.");
    }

    @Test
    public void testFindExistingCustomerReturnsAttachedBike() throws CustomerNotFoundException {
        CustomerDTO found = registry.findCustomer("0701112233");
        assertNotNull(found.getBike(),
                "Returned customer had no bike attached.");
        assertNotNull(found.getBike().getSerialNumber(),
                "Returned bike had no serial number.");
    }

    @Test
    public void testFindUnknownCustomerThrowsCustomerNotFoundException() {
        CustomerNotFoundException thrown = assertThrows(
                CustomerNotFoundException.class,
                () -> registry.findCustomer("0000000000"),
                "findCustomer did not throw CustomerNotFoundException for an unknown phone number.");
        assertEquals("0000000000", thrown.getMissingPhoneNumber(),
                "Exception did not carry the missing phone number.");
        assertTrue(thrown.getMessage().contains("0000000000"),
                "Exception message did not mention the missing phone number.");
    }

    @Test
    public void testFindWithDifferentFormattedNumberThrowsCustomerNotFoundException() {
        assertThrows(CustomerNotFoundException.class,
                () -> registry.findCustomer("070-111 22 33"),
                "findCustomer treated a differently formatted phone number as a match,"
                + " but it should match the stored format exactly.");
    }

    @Test
    public void testFindWithDatabaseFailurePhoneNumberThrowsDatabaseFailureException() {
        DatabaseFailureException thrown = assertThrows(
                DatabaseFailureException.class,
                () -> registry.findCustomer(CustomerRegistry.DATABASE_FAILURE_PHONE_NUMBER),
                "findCustomer did not throw DatabaseFailureException for the hardcoded"
                + " database-failure phone number.");
        assertEquals(CustomerRegistry.DATABASE_FAILURE_PHONE_NUMBER,
                thrown.getFailingIdentifier(),
                "Exception did not carry the failing identifier.");
    }
}
