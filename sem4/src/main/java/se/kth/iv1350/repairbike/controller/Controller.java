package se.kth.iv1350.repairbike.controller;

import java.util.ArrayList;
import java.util.List;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.integration.CustomerNotFoundException;
import se.kth.iv1350.repairbike.integration.CustomerRegistry;
import se.kth.iv1350.repairbike.integration.DatabaseFailureException;
import se.kth.iv1350.repairbike.integration.Printer;
import se.kth.iv1350.repairbike.integration.RegistryCreator;
import se.kth.iv1350.repairbike.integration.RepairOrderRegistry;
import se.kth.iv1350.repairbike.model.DiagnosticReport;
import se.kth.iv1350.repairbike.model.DiscountStrategy;
import se.kth.iv1350.repairbike.model.RepairOrder;
import se.kth.iv1350.repairbike.model.RepairOrderDTO;
import se.kth.iv1350.repairbike.model.RepairOrderObserver;
import se.kth.iv1350.repairbike.model.RepairTask;

/**
 * The application's only controller class. All calls from the view to the
 * model and to the integration layer pass through this class. Every
 * controller method returns an immutable {@link RepairOrderDTO} snapshot
 * instead of the {@link RepairOrder} entity, so the view never holds a
 * reference to mutable model state.
 *
 * <p>The controller is also responsible for registering all
 * {@link RepairOrderObserver}s on every newly created {@link RepairOrder}. The
 * observers are added through {@link #addRepairOrderObserver(RepairOrderObserver)}
 * by the startup code, so that the {@link Controller} does not need to know
 * any concrete view implementation.</p>
 */
public class Controller {
    private final CustomerRegistry customerRegistry;
    private final RepairOrderRegistry repairOrderRegistry;
    private final Printer printer;
    private final List<RepairOrderObserver> repairOrderObservers = new ArrayList<>();

    private RepairOrder currentRepairOrder;

    /**
     * Creates a new instance using the registries owned by the specified
     * {@link RegistryCreator}. This is the constructor production code calls.
     *
     * @param registryCreator Used to obtain references to all registries.
     * @param printer         The printer used when a repair order is printed.
     */
    public Controller(RegistryCreator registryCreator, Printer printer) {
        this(registryCreator.getCustomerRegistry(),
             registryCreator.getRepairOrderRegistry(),
             printer);
    }

    /**
     * Creates a new instance directly from the supplied registries and
     * printer. Intended primarily for unit tests, which can pass isolated
     * registry instances to avoid sharing state through the Singleton.
     *
     * @param customerRegistry    The customer registry to use.
     * @param repairOrderRegistry The repair order registry to use.
     * @param printer             The printer to use.
     */
    public Controller(CustomerRegistry customerRegistry,
                      RepairOrderRegistry repairOrderRegistry,
                      Printer printer) {
        this.customerRegistry = customerRegistry;
        this.repairOrderRegistry = repairOrderRegistry;
        this.printer = printer;
    }

    /**
     * Adds an observer that will be registered on every repair order created
     * by this controller. Observers must be added before any repair order is
     * registered; observers added later will only receive updates to
     * subsequent repair orders.
     *
     * @param observer The observer to register.
     */
    public void addRepairOrderObserver(RepairOrderObserver observer) {
        repairOrderObservers.add(observer);
    }

    /**
     * Removes a previously registered observer. The observer is removed from
     * the controller's list and also from the current repair order, if one
     * exists. Removing an observer that was never registered has no effect.
     *
     * @param observer The observer to remove.
     */
    public void removeRepairOrderObserver(RepairOrderObserver observer) {
        repairOrderObservers.remove(observer);
        if (currentRepairOrder != null) {
            currentRepairOrder.removeRepairOrderObserver(observer);
        }
    }

    /**
     * Searches for the customer with the specified phone number.
     *
     * @param phoneNumber The phone number identifying the customer.
     * @return The customer matching the specified phone number. Never
     *         {@code null}.
     * @throws CustomerNotFoundException If no customer in the registry has
     *                                   the specified phone number.
     * @throws DatabaseFailureException  If the database call could not be
     *                                   completed (simulated by the
     *                                   integration layer for a specific
     *                                   hardcoded phone number).
     */
    public CustomerDTO searchCustomer(String phoneNumber)
            throws CustomerNotFoundException {
        return customerRegistry.findCustomer(phoneNumber);
    }

    /**
     * Creates a new repair order for the specified customer with the
     * specified problem description, and stores the order in the repair
     * order registry. The new order becomes the current repair order.
     *
     * @param customer           The customer that handed in the bike.
     * @param problemDescription The problem description given by the customer.
     * @return An immutable snapshot of the newly created repair order.
     */
    public RepairOrderDTO registerProblem(CustomerDTO customer, String problemDescription) {
        int id = repairOrderRegistry.nextRepairOrderId();
        RepairOrder newOrder = new RepairOrder(id, customer, problemDescription);
        newOrder.addRepairOrderObservers(repairOrderObservers);
        repairOrderRegistry.saveRepairOrder(newOrder);
        currentRepairOrder = newOrder;
        return currentRepairOrder.toDTO();
    }

    /**
     * Adds the technician's diagnostic findings and the proposed repair tasks
     * to the current repair order, and updates the order in the registry.
     *
     * @param diagnosticDescription The technician's diagnostic findings.
     * @param proposedTasks         The repair tasks proposed by the technician.
     * @return An immutable snapshot of the updated repair order.
     */
    public RepairOrderDTO registerDiagnostic(String diagnosticDescription,
                                             List<RepairTask> proposedTasks) {
        DiagnosticReport report = new DiagnosticReport(diagnosticDescription, proposedTasks);
        currentRepairOrder.addDiagnosticReport(report);
        repairOrderRegistry.saveRepairOrder(currentRepairOrder);
        return currentRepairOrder.toDTO();
    }

    /**
     * Sets the discount strategy used by the current repair order. This is
     * typically called after the diagnostic report has been registered, so
     * that the discount is recalculated when the customer is asked about
     * acceptance.
     *
     * @param discountStrategy The discount strategy to use.
     * @return An immutable snapshot of the repair order with the new
     *         discount strategy applied.
     */
    public RepairOrderDTO applyDiscountStrategy(DiscountStrategy discountStrategy) {
        currentRepairOrder.setDiscountStrategy(discountStrategy);
        return currentRepairOrder.toDTO();
    }

    /**
     * Marks the current repair order as accepted by the customer. The order
     * itself asks the printer to print it; the controller is not involved
     * in producing or sending the printout.
     *
     * @return An immutable snapshot of the accepted repair order.
     */
    public RepairOrderDTO acceptRepairOrder() {
        currentRepairOrder.accept(printer);
        repairOrderRegistry.saveRepairOrder(currentRepairOrder);
        return currentRepairOrder.toDTO();
    }
}
