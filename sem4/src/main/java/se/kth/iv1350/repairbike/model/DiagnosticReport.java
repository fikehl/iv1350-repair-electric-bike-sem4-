package se.kth.iv1350.repairbike.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the technician's diagnostic findings for a bike together with the
 * proposed list of repair tasks that follow from those findings.
 */
public class DiagnosticReport {
    private final String description;
    private final List<RepairTask> proposedTasks;

    /**
     * Creates a new instance representing a complete diagnostic report. A
     * defensive copy of {@code proposedTasks} is taken, so subsequent changes
     * to the supplied list do not affect the report.
     *
     * @param description    The technician's written diagnostic findings.
     * @param proposedTasks  The repair tasks that the technician proposes.
     */
    public DiagnosticReport(String description, List<RepairTask> proposedTasks) {
        this.description = description;
        this.proposedTasks = new ArrayList<>(proposedTasks);
    }

    /**
     * @return The written diagnostic findings.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return An unmodifiable view of the proposed repair tasks.
     */
    public List<RepairTask> getProposedTasks() {
        return Collections.unmodifiableList(proposedTasks);
    }

    /**
     * Calculates the total cost of all proposed repair tasks.
     *
     * @return The sum of the costs of every proposed repair task.
     */
    public Amount getTotalCost() {
        Amount totalCost = new Amount(0);
        for (RepairTask task : proposedTasks) {
            totalCost = totalCost.plus(task.getCost());
        }
        return totalCost;
    }
}
