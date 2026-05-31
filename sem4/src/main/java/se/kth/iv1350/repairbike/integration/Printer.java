package se.kth.iv1350.repairbike.integration;

/**
 * Represents the external printer that is used to produce a paper copy of
 * a repair order. In this version of the program no real printer is used,
 * instead the printout is sent to {@code System.out}.
 *
 * <p>The printer is given the formatted printout as a string, so it does not
 * have to know anything about the {@code RepairOrder} entity that produced
 * the string. This keeps the integration layer independent of the model.</p>
 */
public class Printer {

    /**
     * Creates a new instance.
     */
    public Printer() {
    }

    /**
     * Prints the specified pre-formatted repair order printout.
     *
     * @param printout The printout to send to the printer.
     */
    public void printRepairOrder(String printout) {
        System.out.println(printout);
    }
}
