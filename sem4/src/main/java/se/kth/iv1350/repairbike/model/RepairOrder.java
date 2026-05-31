package se.kth.iv1350.repairbike.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.integration.Printer;

/**
 * Represents one specific repair order, created when a customer hands in a
 * bike for repair. The order keeps track of the customer, the bike, the
 * customer's problem description, the technician's diagnostic report and
 * the current state of the order.
 *
 * <p>The class plays the role of "Subject" in the Observer design pattern.
 * Any number of {@link RepairOrderObserver}s can register for updates with
 * {@link #addRepairOrderObserver(RepairOrderObserver)} and unregister with
 * {@link #removeRepairOrderObserver(RepairOrderObserver)}; they are notified
 * through {@link RepairOrderObserver#repairOrderUpdated(RepairOrderDTO)}
 * every time the state of the order changes.</p>
 *
 * <p>The class also collaborates with a {@link DiscountStrategy} (the
 * "Strategy" role) to compute the discounted total of the proposed repair
 * tasks, which lets the discount rule vary independently of this class.</p>
 *
 * <p>The view never sees this entity directly; it only ever sees the
 * immutable {@link RepairOrderDTO} returned by {@link #toDTO()}, so the
 * entity is kept intact.</p>
 */
public class RepairOrder {
    private static final int DEFAULT_REPAIR_DAYS = 7;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int repairOrderId;
    private final CustomerDTO customer;
    private final String problemDescription;
    private final LocalDateTime creationTime;
    private final List<RepairOrderObserver> observers = new ArrayList<>();
    private DiscountStrategy discountStrategy = new NoDiscount();
    private DiagnosticReport diagnosticReport;
    private RepairOrderState state;

    /**
     * Creates a newly registered repair order. The state of the new order is
     * {@link RepairOrderState#NEWLY_CREATED}.
     *
     * @param repairOrderId      A unique id, used to identify this repair order.
     * @param customer           The customer that handed in the bike.
     * @param problemDescription The problem description given by the customer.
     */
    public RepairOrder(int repairOrderId, CustomerDTO customer, String problemDescription) {
        this.repairOrderId = repairOrderId;
        this.customer = customer;
        this.problemDescription = problemDescription;
        this.creationTime = LocalDateTime.now();
        this.state = RepairOrderState.NEWLY_CREATED;
    }

    /**
     * Registers a new observer that shall be notified every time the state of
     * this repair order changes. The observer is added immediately and will
     * receive future updates only; it is not called with the current state.
     *
     * @param observer The observer to add.
     */
    public void addRepairOrderObserver(RepairOrderObserver observer) {
        observers.add(observer);
    }

    /**
     * Registers all observers in the supplied collection. The collection is
     * iterated immediately; subsequent changes to it have no effect on this
     * repair order.
     *
     * @param observersToAdd The observers to add.
     */
    public void addRepairOrderObservers(List<RepairOrderObserver> observersToAdd) {
        for (RepairOrderObserver observer : observersToAdd) {
            addRepairOrderObserver(observer);
        }
    }

    /**
     * Unregisters the specified observer. Subsequent updates to this repair
     * order will no longer be delivered to it. It is safe to call this method
     * with an observer that was never registered; in that case it has no
     * effect.
     *
     * @param observer The observer to remove.
     */
    public void removeRepairOrderObserver(RepairOrderObserver observer) {
        observers.remove(observer);
    }

    /**
     * @return The unique id of this repair order.
     */
    public int getRepairOrderId() {
        return repairOrderId;
    }

    /**
     * @return The current state of this repair order.
     */
    public RepairOrderState getState() {
        return state;
    }

    /**
     * Replaces the discount strategy currently used by this repair order.
     * Setting a new strategy also notifies all registered observers, so that
     * any view showing the order is updated with the new discounted total.
     *
     * @param discountStrategy The new discount strategy. Must not be
     *                         {@code null}.
     * @throws IllegalArgumentException if {@code discountStrategy} is
     *         {@code null}. The repair order's state is unchanged in this
     *         case.
     */
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        if (discountStrategy == null) {
            throw new IllegalArgumentException("Discount strategy must not be null.");
        }
        this.discountStrategy = discountStrategy;
        notifyObservers();
    }

    /**
     * @return The discount strategy currently in use.
     */
    public DiscountStrategy getDiscountStrategy() {
        return discountStrategy;
    }

    /**
     * Adds the technician's diagnostic report and the proposed repair tasks
     * to this order, and changes its state to
     * {@link RepairOrderState#READY_FOR_APPROVAL}. All registered observers
     * are notified after the state change.
     *
     * @param diagnosticReport The diagnostic report that the technician
     *                         produced.
     */
    public void addDiagnosticReport(DiagnosticReport diagnosticReport) {
        this.diagnosticReport = diagnosticReport;
        this.state = RepairOrderState.READY_FOR_APPROVAL;
        notifyObservers();
    }

    /**
     * Marks this repair order as accepted by the customer, notifies all
     * registered observers and asks the specified printer to print this
     * order. Encapsulating the printer call inside this method keeps the
     * printing logic in the model, so that the controller does not need to
     * know how a repair order is printed (compare figure 5.42 in the
     * textbook).
     *
     * @param printer The printer used to produce the paper copy of this
     *                order.
     */
    public void accept(Printer printer) {
        this.state = RepairOrderState.ACCEPTED;
        notifyObservers();
        printer.printRepairOrder(createPrintout());
    }

    /**
     * @return An immutable snapshot of the current state of this order. The
     *         snapshot is the only thing the view and the observers see, so
     *         the entity itself stays intact.
     */
    public RepairOrderDTO toDTO() {
        return createSnapshot();
    }

    private Amount getSubtotal() {
        if (diagnosticReport == null) {
            return new Amount(0);
        }
        return diagnosticReport.getTotalCost();
    }

    private Amount getDiscount() {
        return discountStrategy.calculateDiscount(getSubtotal(), customer);
    }

    private Amount getTotalCost() {
        int discounted = getSubtotal().getValue() - getDiscount().getValue();
        if (discounted < 0) {
            discounted = 0;
        }
        return new Amount(discounted);
    }

    private LocalDateTime getEstimatedCompletionTime() {
        return creationTime.plusDays(DEFAULT_REPAIR_DAYS);
    }

    private String createPrintout() {
        StringBuilder builder = new StringBuilder();
        appendHeader(builder);
        appendCustomerSection(builder);
        appendProblemSection(builder);
        appendDiagnosticSection(builder);
        appendCompletionSection(builder);
        appendFooter(builder);
        return builder.toString();
    }

    private void notifyObservers() {
        RepairOrderDTO snapshot = createSnapshot();
        List<RepairOrderObserver> currentObservers =
                Collections.unmodifiableList(new ArrayList<>(observers));
        for (RepairOrderObserver observer : currentObservers) {
            try {
                observer.repairOrderUpdated(snapshot);
            } catch (Exception observerError) {
                // An observer must never break the subject. Recovery is the
                // observer's responsibility; we only log to stderr so that
                // the failure is at least visible during development.
                System.err.println("Observer " + observer.getClass().getSimpleName()
                                   + " failed: " + observerError.getMessage());
            }
        }
    }

    private RepairOrderDTO createSnapshot() {
        List<RepairTask> snapshotTasks = diagnosticReport == null
                ? List.of()
                : new ArrayList<>(diagnosticReport.getProposedTasks());
        String diagnosticDescription = diagnosticReport == null
                ? null
                : diagnosticReport.getDescription();
        return new RepairOrderDTO(
                repairOrderId,
                customer,
                problemDescription,
                creationTime,
                state,
                diagnosticDescription,
                snapshotTasks,
                getSubtotal(),
                getDiscount(),
                discountStrategy.getDescription(),
                getTotalCost(),
                getEstimatedCompletionTime(),
                createPrintout());
    }

    private void appendHeader(StringBuilder builder) {
        builder.append("============================================\n");
        builder.append("           REPAIR ORDER #").append(repairOrderId).append("\n");
        builder.append("============================================\n");
        builder.append("Date:    ").append(creationTime.format(DATE_TIME_FORMATTER)).append("\n");
        builder.append("State:   ").append(state).append("\n");
        builder.append("--------------------------------------------\n");
    }

    private void appendCustomerSection(StringBuilder builder) {
        builder.append("Customer: ").append(customer.getName()).append("\n");
        builder.append("Phone:    ").append(customer.getPhoneNumber()).append("\n");
        builder.append("Email:    ").append(customer.getEmail()).append("\n");
        builder.append("Bike:     ")
               .append(customer.getBike().getBrand()).append(" ")
               .append(customer.getBike().getModel())
               .append(" (S/N ").append(customer.getBike().getSerialNumber()).append(")\n");
        builder.append("--------------------------------------------\n");
    }

    private void appendProblemSection(StringBuilder builder) {
        builder.append("Customer's problem description:\n");
        builder.append("  ").append(problemDescription).append("\n");
        builder.append("--------------------------------------------\n");
    }

    private void appendDiagnosticSection(StringBuilder builder) {
        if (diagnosticReport == null) {
            builder.append("No diagnostic report has been added yet.\n");
            return;
        }
        builder.append("Diagnostic report:\n");
        builder.append("  ").append(diagnosticReport.getDescription()).append("\n");
        builder.append("\n");
        builder.append("Proposed repair tasks:\n");
        for (RepairTask task : diagnosticReport.getProposedTasks()) {
            builder.append("  - ").append(task).append("\n");
        }
        builder.append("\n");
        builder.append("Subtotal:    ").append(getSubtotal()).append("\n");
        builder.append("Discount:    ").append(getDiscount())
               .append(" (").append(discountStrategy.getDescription()).append(")\n");
        builder.append("Total cost:  ").append(getTotalCost()).append("\n");
        builder.append("--------------------------------------------\n");
    }

    private void appendCompletionSection(StringBuilder builder) {
        builder.append("Estimated completion: ")
               .append(getEstimatedCompletionTime().format(DATE_TIME_FORMATTER))
               .append("\n");
    }

    private void appendFooter(StringBuilder builder) {
        builder.append("============================================\n");
    }
}
