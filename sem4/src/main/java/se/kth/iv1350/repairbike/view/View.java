package se.kth.iv1350.repairbike.view;

import java.util.ArrayList;
import java.util.List;
import se.kth.iv1350.repairbike.controller.Controller;
import se.kth.iv1350.repairbike.integration.CustomerDTO;
import se.kth.iv1350.repairbike.integration.CustomerNotFoundException;
import se.kth.iv1350.repairbike.integration.CustomerRegistry;
import se.kth.iv1350.repairbike.integration.DatabaseFailureException;
import se.kth.iv1350.repairbike.model.Amount;
import se.kth.iv1350.repairbike.model.LoyaltyDiscount;
import se.kth.iv1350.repairbike.model.RepairOrderDTO;
import se.kth.iv1350.repairbike.model.RepairTask;
import se.kth.iv1350.repairbike.util.ErrorFileLogger;

/**
 * This program has no real view, this class instead simulates user input by
 * making hard-coded calls to the controller. Everything that is returned by
 * the controller is printed to {@code System.out}.
 *
 * <p>The view is responsible for displaying informative messages to the user
 * when an exception is caught, and for writing a detailed error report to a
 * log file when the exception indicates that the program is not functioning
 * as intended.</p>
 */
public class View {
    private final Controller controller;
    private final ErrorFileLogger errorLogger;

    /**
     * Creates a new instance using a default {@link ErrorFileLogger}.
     *
     * @param controller The controller used for all calls to the model.
     */
    public View(Controller controller) {
        this(controller, new ErrorFileLogger());
    }

    /**
     * Creates a new instance.
     *
     * @param controller  The controller used for all calls to the model.
     * @param errorLogger The logger used to record technical error details
     *                    intended for developers.
     */
    public View(Controller controller, ErrorFileLogger errorLogger) {
        this.controller = controller;
        this.errorLogger = errorLogger;
    }

    /**
     * Performs a sample execution that walks through the basic flow of the
     * Repair Electric Bike use case from start to end, including both
     * exception scenarios (an unknown phone number, and a database failure).
     */
    public void sampleExecution() {
        runNormalFlow();
        runUnknownCustomerFlow();
        runDatabaseFailureFlow();
    }

    private void runNormalFlow() {
        printSeparator();
        System.out.println("RECEPTIONIST: Customer arrives. Searching for customer with"
                           + " phone 0701112233.");
        CustomerDTO customer = trySearchCustomer("0701112233");
        if (customer == null) {
            return;
        }
        System.out.println("System returned: " + customer);

        printSeparator();
        System.out.println("RECEPTIONIST: Customer describes the problem. Registering it.");
        String problemDescription =
                "The motor cuts out after about ten minutes of use, and the front brake"
                + " squeaks loudly when applied.";
        RepairOrderDTO afterProblem = controller.registerProblem(customer, problemDescription);
        System.out.println("System returned a new repair order:");
        System.out.println(afterProblem.getPrintout());

        printSeparator();
        System.out.println("TECHNICIAN: Performing diagnostic and proposing repair tasks.");
        String diagnosticDescription =
                "The battery management system has a faulty temperature sensor that"
                + " triggers an emergency shutdown. The front brake pads are worn"
                + " down to the wear indicators.";
        List<RepairTask> proposedTasks = buildProposedTasks();
        RepairOrderDTO afterDiagnostic =
                controller.registerDiagnostic(diagnosticDescription, proposedTasks);
        System.out.println("System returned the updated repair order:");
        System.out.println(afterDiagnostic.getPrintout());

        printSeparator();
        System.out.println("RECEPTIONIST: Anna is a returning customer; applying loyalty discount.");
        controller.applyDiscountStrategy(new LoyaltyDiscount());

        printSeparator();
        System.out.println("RECEPTIONIST: Customer accepts the proposed repair tasks."
                           + " Registering acceptance.");
        RepairOrderDTO afterAcceptance = controller.acceptRepairOrder();
        System.out.println("System returned the accepted repair order:");
        System.out.println(afterAcceptance.getPrintout());
        printSeparator();
    }

    private void runUnknownCustomerFlow() {
        printSeparator();
        System.out.println("RECEPTIONIST: A walk-in customer gives phone 0999999999."
                           + " Searching the registry.");
        trySearchCustomer("0999999999");
        printSeparator();
    }

    private void runDatabaseFailureFlow() {
        printSeparator();
        System.out.println("RECEPTIONIST: Searching for customer with phone "
                           + CustomerRegistry.DATABASE_FAILURE_PHONE_NUMBER
                           + " (simulates an unreachable database).");
        trySearchCustomer(CustomerRegistry.DATABASE_FAILURE_PHONE_NUMBER);
        printSeparator();
    }

    /**
     * Wraps {@link Controller#searchCustomer(String)} with the view-level
     * error handling required by seminar 4: business errors are shown to the
     * user as informative messages; technical errors are also written to the
     * error log so developers can investigate.
     *
     * @param phoneNumber The phone number to search for.
     * @return The customer if the lookup succeeded, or {@code null} if any
     *         exception was caught.
     */
    private CustomerDTO trySearchCustomer(String phoneNumber) {
        try {
            CustomerDTO customer = controller.searchCustomer(phoneNumber);
            return customer;
        } catch (CustomerNotFoundException notFound) {
            System.out.println("Sorry, no customer was found for phone number "
                               + notFound.getMissingPhoneNumber() + "."
                               + " Please double-check the number and try again.");
            return null;
        } catch (DatabaseFailureException dbFailure) {
            System.out.println("The customer database is temporarily unavailable."
                               + " Please try again in a few minutes, or contact"
                               + " IT support if the problem persists.");
            errorLogger.logException("Database failure while searching for phone "
                                     + dbFailure.getFailingIdentifier(), dbFailure);
            return null;
        }
    }

    private List<RepairTask> buildProposedTasks() {
        List<RepairTask> tasks = new ArrayList<>();
        tasks.add(new RepairTask("Replace battery temperature sensor", new Amount(1200)));
        tasks.add(new RepairTask("Replace front brake pads", new Amount(450)));
        tasks.add(new RepairTask("Full safety inspection", new Amount(350)));
        return tasks;
    }

    private void printSeparator() {
        System.out.println();
        System.out.println("############################################"
                           + "############################");
        System.out.println();
    }
}
