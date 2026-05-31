package se.kth.iv1350.repairbike.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import se.kth.iv1350.repairbike.integration.CustomerDTO;

/**
 * An immutable snapshot of a {@link RepairOrder}. Instances are created by
 * {@link RepairOrder#toDTO()} and are the only thing the view and the
 * observers see of a repair order, so the model entity is kept intact.
 *
 * <p>The same DTO is used both as the return type of the {@code Controller}
 * methods and as the payload of the observer notification. This keeps the
 * snapshot definition in one place and means observers never have to call
 * back into the model for additional data.</p>
 */
public final class RepairOrderDTO {
    private final int repairOrderId;
    private final CustomerDTO customer;
    private final String problemDescription;
    private final LocalDateTime creationTime;
    private final RepairOrderState state;
    private final String diagnosticDescription;
    private final List<RepairTask> proposedTasks;
    private final Amount subtotal;
    private final Amount discount;
    private final String discountDescription;
    private final Amount totalCost;
    private final LocalDateTime estimatedCompletionTime;
    private final String printout;

    /**
     * Creates a new snapshot. The constructor is package-private because
     * snapshots are only produced inside the model.
     *
     * @param repairOrderId           The id of the snapshotted repair order.
     * @param customer                The customer that handed in the bike.
     * @param problemDescription      The customer's problem description.
     * @param creationTime            The time when the order was created.
     * @param state                   The current state.
     * @param diagnosticDescription   The technician's diagnostic findings, or
     *                                {@code null} if no diagnostic report has
     *                                been added yet.
     * @param proposedTasks           The proposed repair tasks; may be empty.
     * @param subtotal                The total of the proposed tasks before
     *                                any discount is applied.
     * @param discount                The discount produced by the current
     *                                discount strategy.
     * @param discountDescription     A short, human-readable description of
     *                                the current discount strategy.
     * @param totalCost               The discounted total cost.
     * @param estimatedCompletionTime The estimated completion time.
     * @param printout                A pre-formatted, human-readable printout
     *                                of the entire repair order.
     */
    RepairOrderDTO(int repairOrderId,
                   CustomerDTO customer,
                   String problemDescription,
                   LocalDateTime creationTime,
                   RepairOrderState state,
                   String diagnosticDescription,
                   List<RepairTask> proposedTasks,
                   Amount subtotal,
                   Amount discount,
                   String discountDescription,
                   Amount totalCost,
                   LocalDateTime estimatedCompletionTime,
                   String printout) {
        this.repairOrderId = repairOrderId;
        this.customer = customer;
        this.problemDescription = problemDescription;
        this.creationTime = creationTime;
        this.state = state;
        this.diagnosticDescription = diagnosticDescription;
        this.proposedTasks = Collections.unmodifiableList(proposedTasks);
        this.subtotal = subtotal;
        this.discount = discount;
        this.discountDescription = discountDescription;
        this.totalCost = totalCost;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.printout = printout;
    }

    /**
     * @return The id of the snapshotted repair order.
     */
    public int getRepairOrderId() {
        return repairOrderId;
    }

    /**
     * @return The customer that handed in the bike.
     */
    public CustomerDTO getCustomer() {
        return customer;
    }

    /**
     * @return The customer's problem description.
     */
    public String getProblemDescription() {
        return problemDescription;
    }

    /**
     * @return The time when the order was created.
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * @return The state of the order at the time the snapshot was taken.
     */
    public RepairOrderState getState() {
        return state;
    }

    /**
     * @return The technician's diagnostic findings, or {@code null} if no
     *         diagnostic report has been added yet.
     */
    public String getDiagnosticDescription() {
        return diagnosticDescription;
    }

    /**
     * @return An unmodifiable view of the proposed repair tasks. The list is
     *         empty if no diagnostic report has been added.
     */
    public List<RepairTask> getProposedTasks() {
        return proposedTasks;
    }

    /**
     * @return The total of the proposed repair tasks before discount.
     */
    public Amount getSubtotal() {
        return subtotal;
    }

    /**
     * @return The discount produced by the current discount strategy.
     */
    public Amount getDiscount() {
        return discount;
    }

    /**
     * @return A short, human-readable description of the current discount
     *         strategy.
     */
    public String getDiscountDescription() {
        return discountDescription;
    }

    /**
     * @return The discounted total cost of the proposed repair tasks.
     */
    public Amount getTotalCost() {
        return totalCost;
    }

    /**
     * @return The estimated completion time.
     */
    public LocalDateTime getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }

    /**
     * @return A pre-formatted, human-readable printout of the entire order.
     */
    public String getPrintout() {
        return printout;
    }
}
