package se.kth.iv1350.repairbike.view;

import java.io.PrintStream;
import se.kth.iv1350.repairbike.model.RepairOrderDTO;
import se.kth.iv1350.repairbike.model.RepairOrderObserver;

/**
 * Observer that prints every update to a repair order on a {@link PrintStream}
 * (by default, {@code System.out}). The motivation for this view is that it
 * lets the technician see new repair orders the moment the receptionist
 * registers them, and lets the receptionist see that the technician has
 * completed the diagnostic report, without having to ask the system.
 *
 * <p>This class is one of the two implementations of
 * {@link RepairOrderObserver}; it never calls back into the controller or
 * the model. All information it shows comes from the {@link RepairOrderDTO}
 * snapshot that the observed repair order pushes to it.</p>
 */
public class RepairOrderView implements RepairOrderObserver {
    private final PrintStream output;

    /**
     * Creates a new instance that writes to {@code System.out}.
     */
    public RepairOrderView() {
        this(System.out);
    }

    /**
     * Creates a new instance that writes to the specified stream.
     *
     * @param output The stream to print updates to.
     */
    public RepairOrderView(PrintStream output) {
        this.output = output;
    }

    /**
     * Prints the updated repair order to the output stream.
     */
    @Override
    public void repairOrderUpdated(RepairOrderDTO snapshot) {
        output.println();
        output.println(">>> RepairOrderView: repair order #"
                       + snapshot.getRepairOrderId() + " updated"
                       + " (state = " + snapshot.getState() + ")");
        output.println(snapshot.getPrintout());
    }
}
