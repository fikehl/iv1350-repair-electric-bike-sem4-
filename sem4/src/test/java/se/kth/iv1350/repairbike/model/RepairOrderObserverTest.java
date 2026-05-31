package se.kth.iv1350.repairbike.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.integration.BikeDTO;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.integration.Printer;

/**
 * Unit tests for the Observer pattern implemented in {@link RepairOrder}. A
 * recording observer is used as a stand-in for the real
 * {@code RepairOrderView} and {@code RepairOrderLogger}, so that the test
 * can assert exactly how many updates each observer received and in what
 * order.
 */
public class RepairOrderObserverTest {
    private RepairOrder repairOrder;
    private RecordingObserver firstObserver;
    private RecordingObserver secondObserver;

    @BeforeEach
    public void setUp() {
        CustomerDTO customer = new CustomerDTO(
                "Anna Andersson", "0701112233", "anna@example.com",
                new BikeDTO("Crescent", "Elina E8", "CR-E8-00417"));
        repairOrder = new RepairOrder(1, customer, "Brakes squeaking.");
        firstObserver = new RecordingObserver();
        secondObserver = new RecordingObserver();
    }

    @Test
    public void testAddingDiagnosticReportNotifiesObserver() {
        repairOrder.addRepairOrderObserver(firstObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        assertEquals(1, firstObserver.snapshots.size(),
                "Observer was not notified after diagnostic report was added.");
        assertEquals(RepairOrderState.READY_FOR_APPROVAL,
                firstObserver.snapshots.get(0).getState(),
                "Snapshot did not carry the new state.");
    }

    @Test
    public void testAcceptNotifiesObserver() {
        repairOrder.addRepairOrderObserver(firstObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        repairOrder.accept(new Printer());
        assertEquals(2, firstObserver.snapshots.size(),
                "Observer should have been notified twice (once for diagnostic, once for accept).");
        assertEquals(RepairOrderState.ACCEPTED,
                firstObserver.snapshots.get(1).getState(),
                "Last snapshot did not have state ACCEPTED.");
    }

    @Test
    public void testAllRegisteredObserversAreNotified() {
        repairOrder.addRepairOrderObserver(firstObserver);
        repairOrder.addRepairOrderObserver(secondObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        assertEquals(1, firstObserver.snapshots.size(),
                "First observer did not get notified.");
        assertEquals(1, secondObserver.snapshots.size(),
                "Second observer did not get notified.");
    }

    @Test
    public void testSnapshotCarriesPrintoutAndTotalCost() {
        repairOrder.addRepairOrderObserver(firstObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        RepairOrderDTO snapshot = firstObserver.snapshots.get(0);
        assertEquals(1, snapshot.getRepairOrderId(),
                "Snapshot did not carry the repair order id.");
        assertTrue(snapshot.getPrintout().contains("Anna Andersson"),
                "Snapshot printout did not include the customer's name.");
        assertEquals(new Amount(650), snapshot.getTotalCost(),
                "Snapshot did not carry the correct total cost.");
    }

    @Test
    public void testObserversAreNotNotifiedBeforeAnyUpdate() {
        repairOrder.addRepairOrderObserver(firstObserver);
        assertTrue(firstObserver.snapshots.isEmpty(),
                "Observer was notified before any update was made.");
    }

    @Test
    public void testFailingObserverDoesNotPreventOtherObservers() {
        RepairOrderObserver failing = snapshot -> {
            throw new RuntimeException("simulated observer failure");
        };
        repairOrder.addRepairOrderObserver(failing);
        repairOrder.addRepairOrderObserver(secondObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        assertEquals(1, secondObserver.snapshots.size(),
                "A failure in one observer prevented later observers from being"
                + " notified. The subject must be defensive.");
    }

    @Test
    public void testChangingDiscountStrategyNotifiesObservers() {
        repairOrder.addRepairOrderObserver(firstObserver);
        repairOrder.setDiscountStrategy(new LoyaltyDiscount());
        assertEquals(1, firstObserver.snapshots.size(),
                "Observers were not notified when the discount strategy changed.");
    }

    @Test
    public void testRemovedObserverIsNoLongerNotified() {
        repairOrder.addRepairOrderObserver(firstObserver);
        repairOrder.addRepairOrderObserver(secondObserver);
        repairOrder.removeRepairOrderObserver(firstObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        assertTrue(firstObserver.snapshots.isEmpty(),
                "Removed observer received an update after being removed.");
        assertEquals(1, secondObserver.snapshots.size(),
                "Remaining observer should still have been notified.");
    }

    @Test
    public void testRemovingUnknownObserverIsHarmless() {
        repairOrder.addRepairOrderObserver(firstObserver);
        // secondObserver was never added; removing it must be a no-op.
        repairOrder.removeRepairOrderObserver(secondObserver);
        repairOrder.addDiagnosticReport(buildSampleReport());
        assertEquals(1, firstObserver.snapshots.size(),
                "Registered observer should still have been notified.");
    }

    private DiagnosticReport buildSampleReport() {
        List<RepairTask> tasks = new ArrayList<>();
        tasks.add(new RepairTask("Replace front brake pads", new Amount(450)));
        tasks.add(new RepairTask("Adjust gears", new Amount(200)));
        return new DiagnosticReport("Brakes worn, gears slip.", tasks);
    }

    private static final class RecordingObserver implements RepairOrderObserver {
        private final List<RepairOrderDTO> snapshots = new ArrayList<>();

        @Override
        public void repairOrderUpdated(RepairOrderDTO snapshot) {
            snapshots.add(snapshot);
        }
    }
}
