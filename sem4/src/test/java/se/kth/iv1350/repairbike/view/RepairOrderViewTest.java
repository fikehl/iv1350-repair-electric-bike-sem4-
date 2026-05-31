package se.kth.iv1350.repairbike.view;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.integration.BikeDTO;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.model.Amount;
import se.kth.iv1350.repairbike.model.DiagnosticReport;
import se.kth.iv1350.repairbike.model.RepairOrder;
import se.kth.iv1350.repairbike.model.RepairTask;

/**
 * Unit tests for {@link RepairOrderView}. The view is wired up as an
 * observer on a real {@link RepairOrder} and its output is captured to
 * a {@link ByteArrayOutputStream}.
 */
public class RepairOrderViewTest {

    @Test
    public void testViewPrintsWhenRepairOrderIsUpdated() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        RepairOrderView view = new RepairOrderView(new PrintStream(buffer));
        CustomerDTO customer = new CustomerDTO(
                "Anna Andersson", "0701112233", "anna@example.com",
                new BikeDTO("Crescent", "Elina E8", "CR-E8-00417"));
        RepairOrder repairOrder = new RepairOrder(7, customer, "Brakes squeaking.");
        repairOrder.addRepairOrderObserver(view);

        repairOrder.addDiagnosticReport(new DiagnosticReport("Worn pads.",
                java.util.List.of(new RepairTask("Replace pads", new Amount(450)))));

        String output = buffer.toString();
        assertTrue(output.contains("repair order #7"),
                "View output did not mention the updated repair order id.");
        assertTrue(output.contains("READY_FOR_APPROVAL"),
                "View output did not mention the new state.");
        assertTrue(output.contains("Anna Andersson"),
                "View output did not contain the customer's name from the printout.");
    }
}
