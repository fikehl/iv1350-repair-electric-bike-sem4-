package se.kth.iv1350.repairbike.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.integration.BikeDTO;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.integration.Printer;
import se.kth.iv1350.repairbike.model.Amount;
import se.kth.iv1350.repairbike.model.DiagnosticReport;
import se.kth.iv1350.repairbike.model.RepairOrder;
import se.kth.iv1350.repairbike.model.RepairTask;

/**
 * Unit tests for {@link RepairOrderLogger}, which writes repair-order updates
 * to a log file. The tests use a temporary file so the real log on disk is
 * not modified.
 */
public class RepairOrderLoggerTest {
    private Path tempLog;

    @BeforeEach
    public void setUp() throws IOException {
        tempLog = Files.createTempFile("repair-order-logger-test", ".log");
        Files.deleteIfExists(tempLog);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempLog);
    }

    @Test
    public void testLoggerCreatesFileAndWritesUpdate() throws IOException {
        RepairOrderLogger logger = new RepairOrderLogger(tempLog);
        CustomerDTO customer = new CustomerDTO(
                "Anna Andersson", "0701112233", "anna@example.com",
                new BikeDTO("Crescent", "Elina E8", "CR-E8-00417"));
        RepairOrder repairOrder = new RepairOrder(11, customer, "Brakes squeaking.");
        repairOrder.addRepairOrderObserver(logger);

        repairOrder.addDiagnosticReport(new DiagnosticReport("Worn pads.",
                java.util.List.of(new RepairTask("Replace pads", new Amount(450)))));

        assertTrue(Files.exists(tempLog),
                "RepairOrderLogger did not create the log file.");
        String content = Files.readString(tempLog);
        assertTrue(content.contains("repair order #11"),
                "Log file did not mention the updated repair order id.");
        assertTrue(content.contains("Anna Andersson"),
                "Log file did not contain the customer's name from the printout.");
    }

    @Test
    public void testLoggerAppendsMultipleEntries() throws IOException {
        RepairOrderLogger logger = new RepairOrderLogger(tempLog);
        CustomerDTO customer = new CustomerDTO(
                "Anna Andersson", "0701112233", "anna@example.com",
                new BikeDTO("Crescent", "Elina E8", "CR-E8-00417"));
        RepairOrder repairOrder = new RepairOrder(11, customer, "Brakes squeaking.");
        repairOrder.addRepairOrderObserver(logger);

        repairOrder.addDiagnosticReport(new DiagnosticReport("Worn pads.",
                java.util.List.of(new RepairTask("Replace pads", new Amount(450)))));
        repairOrder.accept(new Printer());

        String content = Files.readString(tempLog);
        int firstUpdateIndex = content.indexOf("READY_FOR_APPROVAL");
        int secondUpdateIndex = content.indexOf("ACCEPTED");
        assertTrue(firstUpdateIndex >= 0 && secondUpdateIndex > firstUpdateIndex,
                "Log file did not contain both state transitions in order.");
    }
}
