package se.kth.iv1350.repairbike.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.controller.Controller;
import se.kth.iv1350.repairbike.integration.CustomerRegistry;
import se.kth.iv1350.repairbike.integration.Printer;
import se.kth.iv1350.repairbike.integration.RegistryCreator;
import se.kth.iv1350.repairbike.util.ErrorFileLogger;

/**
 * Unit tests for {@link View}'s exception handling. The view is given a
 * controller backed by an isolated {@link RegistryCreator} and an
 * {@link ErrorFileLogger} that writes to a temporary file, so the tests can
 * verify both (a) what the user sees on {@code System.out} and (b) whether
 * the developer log was written for each exception path.
 */
public class ViewTest {
    private Path tempLog;
    private ErrorFileLogger errorLogger;
    private PrintStream originalOut;
    private ByteArrayOutputStream captured;

    @BeforeEach
    public void setUp() throws IOException {
        tempLog = Files.createTempFile("view-test", ".log");
        Files.deleteIfExists(tempLog);
        errorLogger = new ErrorFileLogger(tempLog);
        originalOut = System.out;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    public void tearDown() throws IOException {
        System.setOut(originalOut);
        Files.deleteIfExists(tempLog);
    }

    @Test
    public void testSampleExecutionShowsFriendlyMessageForUnknownCustomer() {
        runSampleExecution();
        String output = captured.toString();
        assertTrue(output.contains("Sorry, no customer was found for phone number 0999999999"),
                "View did not show the user-facing message for the unknown phone number.");
    }

    @Test
    public void testSampleExecutionShowsFriendlyMessageForDatabaseFailure() {
        runSampleExecution();
        String output = captured.toString();
        assertTrue(output.contains("The customer database is temporarily unavailable"),
                "View did not show the user-facing message for the database failure.");
    }

    @Test
    public void testSampleExecutionUserMessagesDoNotLeakTechnicalDetails() {
        runSampleExecution();
        String output = captured.toString();
        assertFalse(output.contains("DatabaseFailureException"),
                "User-facing output should not contain the exception class name.");
        assertFalse(output.contains("at se.kth.iv1350.repairbike"),
                "User-facing output should not contain a stack trace.");
    }

    @Test
    public void testDatabaseFailureIsLoggedForDevelopers() throws IOException {
        runSampleExecution();
        assertTrue(Files.exists(tempLog),
                "Error log file was not created on a database failure.");
        String logContent = Files.readString(tempLog);
        assertTrue(logContent.contains("Database failure while searching for phone 0700000000"),
                "Error log did not contain the message for the database failure.");
        assertTrue(logContent.contains("DatabaseFailureException"),
                "Error log did not contain the exception class name.");
    }

    @Test
    public void testCustomerNotFoundIsNotLogged() throws IOException {
        runSampleExecution();
        String logContent = Files.exists(tempLog) ? Files.readString(tempLog) : "";
        assertFalse(logContent.contains("0999999999"),
                "Error log must not contain the unknown phone number, since"
                + " a missing customer is a business condition, not a developer-relevant error.");
    }

    private void runSampleExecution() {
        Controller controller = new Controller(
                RegistryCreator.createForTests(), new Printer());
        View view = new View(controller, errorLogger);
        view.sampleExecution();
    }

    @Test
    public void testKnownPhoneNumberStillWorksInSampleExecution() {
        runSampleExecution();
        String output = captured.toString();
        assertTrue(output.contains("Anna Andersson"),
                "Sample execution did not print the known customer's name.");
        assertTrue(output.contains(CustomerRegistry.DATABASE_FAILURE_PHONE_NUMBER),
                "Sample execution did not include the database-failure phone number.");
    }
}
