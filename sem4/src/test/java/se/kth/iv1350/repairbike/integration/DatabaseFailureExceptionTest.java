package se.kth.iv1350.repairbike.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DatabaseFailureException}.
 */
public class DatabaseFailureExceptionTest {

    @Test
    public void testExceptionStoresFailingIdentifier() {
        DatabaseFailureException exception = new DatabaseFailureException("ABC-42");
        assertEquals("ABC-42", exception.getFailingIdentifier(),
                "Exception did not store the failing identifier.");
    }

    @Test
    public void testExceptionMessageMentionsFailingIdentifier() {
        DatabaseFailureException exception = new DatabaseFailureException("ABC-42");
        assertTrue(exception.getMessage().contains("ABC-42"),
                "Exception message did not mention the failing identifier.");
    }

    @Test
    public void testExceptionWrapsCause() {
        Throwable cause = new IllegalStateException("simulated cause");
        DatabaseFailureException exception = new DatabaseFailureException("ABC-42", cause);
        assertSame(cause, exception.getCause(),
                "Exception did not preserve the underlying cause.");
    }

    @Test
    public void testDatabaseFailureExceptionIsUnchecked() {
        assertTrue(RuntimeException.class.isAssignableFrom(DatabaseFailureException.class),
                "DatabaseFailureException must be unchecked (extend RuntimeException).");
    }
}
