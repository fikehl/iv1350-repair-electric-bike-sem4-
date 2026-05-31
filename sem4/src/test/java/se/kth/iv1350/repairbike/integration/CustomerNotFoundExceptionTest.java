package se.kth.iv1350.repairbike.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CustomerNotFoundException}.
 */
public class CustomerNotFoundExceptionTest {

    @Test
    public void testExceptionStoresMissingPhoneNumber() {
        CustomerNotFoundException exception = new CustomerNotFoundException("0123456789");
        assertEquals("0123456789", exception.getMissingPhoneNumber(),
                "Exception did not store the missing phone number.");
    }

    @Test
    public void testExceptionMessageMentionsMissingPhoneNumber() {
        CustomerNotFoundException exception = new CustomerNotFoundException("0123456789");
        assertTrue(exception.getMessage().contains("0123456789"),
                "Exception message did not mention the missing phone number.");
    }

    @Test
    public void testCustomerNotFoundExceptionIsChecked() {
        assertFalse(RuntimeException.class.isAssignableFrom(CustomerNotFoundException.class),
                "CustomerNotFoundException must be a checked exception, but it"
                + " extends RuntimeException.");
        assertTrue(Exception.class.isAssignableFrom(CustomerNotFoundException.class),
                "CustomerNotFoundException must extend Exception.");
    }
}
