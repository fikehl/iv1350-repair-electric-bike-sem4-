package se.kth.iv1350.repairbike.model;

/**
 * Represents one specific repair task proposed by a technician, for example
 * "Replace front brake pads". Instances are immutable.
 */
public final class RepairTask {
    private final String description;
    private final Amount cost;

    /**
     * Creates a new instance representing the specified repair task.
     *
     * @param description A short description of the work to be performed.
     * @param cost        The estimated cost of performing this repair task.
     */
    public RepairTask(String description, Amount cost) {
        this.description = description;
        this.cost = cost;
    }

    /**
     * @return A short description of this repair task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The estimated cost of performing this repair task.
     */
    public Amount getCost() {
        return cost;
    }

    /**
     * @return A textual representation of this repair task, on the form
     *         "description: cost".
     */
    @Override
    public String toString() {
        return description + ": " + cost;
    }
}
