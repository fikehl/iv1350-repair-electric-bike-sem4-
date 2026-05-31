package se.kth.iv1350.repairbike.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.integration.BikeDTO;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.integration.Printer;

/**
 * Unit tests for {@link RepairOrder}. The repair order entity is read by the
 * tests through its {@link RepairOrder#toDTO()} snapshot, so the test does
 * not have to break encapsulation by adding extra getters to the SUT.
 */
public class RepairOrderTest {
    private CustomerDTO customer;
    private RepairOrder repairOrder;

    @BeforeEach
    public void setUp() {
        BikeDTO bike = new BikeDTO("Crescent", "Elina E8", "CR-E8-00417");
        customer = new CustomerDTO("Anna Andersson", "0701112233",
                                   "anna@example.com", bike);
        repairOrder = new RepairOrder(42, customer, "Brakes squeaking.");
    }

    @AfterEach
    public void tearDown() {
        customer = null;
        repairOrder = null;
    }

    @Test
    public void testInitialStateIsNewlyCreated() {
        assertEquals(RepairOrderState.NEWLY_CREATED, repairOrder.getState(),
                "A newly created repair order did not have state NEWLY_CREATED.");
    }

    @Test
    public void testToDTOOfNewOrderHasNoDiagnosticDescription() {
        assertNull(repairOrder.toDTO().getDiagnosticDescription(),
                "A newly created repair order had a diagnostic description.");
    }

    @Test
    public void testToDTOOfNewOrderHasNoProposedTasks() {
        assertTrue(repairOrder.toDTO().getProposedTasks().isEmpty(),
                "A newly created repair order had proposed tasks.");
    }

    @Test
    public void testInitialTotalCostIsZero() {
        assertEquals(new Amount(0), repairOrder.toDTO().getTotalCost(),
                "Total cost of a newly created repair order was not zero.");
    }

    @Test
    public void testCustomerIsStored() {
        assertSame(customer, repairOrder.toDTO().getCustomer(),
                "The customer passed to the constructor was not stored.");
    }

    @Test
    public void testProblemDescriptionIsStored() {
        assertEquals("Brakes squeaking.", repairOrder.toDTO().getProblemDescription(),
                "The problem description passed to the constructor was not stored.");
    }

    @Test
    public void testCreationTimeIsCloseToNow() {
        long secondsBetween = ChronoUnit.SECONDS.between(
                repairOrder.toDTO().getCreationTime(), LocalDateTime.now());
        assertTrue(secondsBetween < 5,
                "Creation time was not close to the current time.");
    }

    @Test
    public void testAddDiagnosticReportChangesStateToReadyForApproval() {
        repairOrder.addDiagnosticReport(buildSampleReport());
        assertEquals(RepairOrderState.READY_FOR_APPROVAL, repairOrder.getState(),
                "State did not change to READY_FOR_APPROVAL after adding a diagnostic report.");
    }

    @Test
    public void testToDTOAfterAddingReportContainsDiagnosticDescription() {
        DiagnosticReport report = buildSampleReport();
        repairOrder.addDiagnosticReport(report);
        assertEquals(report.getDescription(),
                repairOrder.toDTO().getDiagnosticDescription(),
                "DTO did not contain the diagnostic description after a report was added.");
    }

    @Test
    public void testTotalCostMatchesDiagnosticReportAfterAdding() {
        DiagnosticReport report = buildSampleReport();
        repairOrder.addDiagnosticReport(report);
        assertEquals(report.getTotalCost(),
                repairOrder.toDTO().getTotalCost(),
                "Total cost of the repair order did not match the cost of the diagnostic report.");
    }

    @Test
    public void testAcceptChangesStateToAccepted() {
        repairOrder.addDiagnosticReport(buildSampleReport());
        repairOrder.accept(new Printer());
        assertEquals(RepairOrderState.ACCEPTED, repairOrder.getState(),
                "State did not change to ACCEPTED after calling accept.");
    }

    @Test
    public void testAcceptSendsPrintoutToPrinter() {
        repairOrder.addDiagnosticReport(buildSampleReport());
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            repairOrder.accept(new Printer());
        } finally {
            System.setOut(originalOut);
        }
        String printed = captured.toString();
        assertTrue(printed.contains("REPAIR ORDER #42"),
                "Printer output did not contain the repair order header.");
        assertTrue(printed.contains("ACCEPTED"),
                "Printer output did not show that the order was ACCEPTED.");
    }

    @Test
    public void testEstimatedCompletionIsAfterCreation() {
        RepairOrderDTO snapshot = repairOrder.toDTO();
        assertTrue(snapshot.getEstimatedCompletionTime()
                           .isAfter(snapshot.getCreationTime()),
                "Estimated completion time was not after the creation time.");
    }

    @Test
    public void testPrintoutContainsKeyInformation() {
        repairOrder.addDiagnosticReport(buildSampleReport());
        repairOrder.accept(new Printer());
        String printout = repairOrder.toDTO().getPrintout();
        assertTrue(printout.contains("42"),
                "Printout did not contain the repair order id.");
        assertTrue(printout.contains("Anna Andersson"),
                "Printout did not contain the customer's name.");
        assertTrue(printout.contains("0701112233"),
                "Printout did not contain the customer's phone number.");
        assertTrue(printout.contains("Crescent"),
                "Printout did not contain the bike's brand.");
        assertTrue(printout.contains("Brakes squeaking."),
                "Printout did not contain the customer's problem description.");
        assertTrue(printout.contains("ACCEPTED"),
                "Printout did not contain the repair order state.");
        assertTrue(printout.contains("Total cost"),
                "Printout did not contain the total cost line.");
    }

    @Test
    public void testPrintoutMentionsMissingDiagnosticBeforeItIsAdded() {
        String printout = repairOrder.toDTO().getPrintout();
        assertTrue(printout.toLowerCase().contains("no diagnostic"),
                "Printout did not indicate that no diagnostic report was added yet.");
    }

    private DiagnosticReport buildSampleReport() {
        List<RepairTask> tasks = new ArrayList<>();
        tasks.add(new RepairTask("Replace front brake pads", new Amount(450)));
        tasks.add(new RepairTask("Adjust gears", new Amount(200)));
        return new DiagnosticReport("Brakes worn, gears slip.", tasks);
    }
}
