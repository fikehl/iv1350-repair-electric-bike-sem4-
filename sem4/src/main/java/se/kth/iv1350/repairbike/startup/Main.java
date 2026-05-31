package se.kth.iv1350.repairbike.startup;

import se.kth.iv1350.repairbike.controller.Controller;
import se.kth.iv1350.repairbike.integration.Printer;
import se.kth.iv1350.repairbike.integration.RegistryCreator;
import se.kth.iv1350.repairbike.view.RepairOrderLogger;
import se.kth.iv1350.repairbike.view.RepairOrderView;
import se.kth.iv1350.repairbike.view.View;

/**
 * Contains the {@code main} method. Performs all startup of the application.
 *
 * <p>The startup code is responsible for instantiating all observers that
 * shall be notified of repair order updates and for registering them on the
 * {@link Controller}. The controller, in turn, attaches them to every
 * repair order it creates. The {@link RegistryCreator} is obtained through
 * its {@code getInstance} method, since it is a Singleton.</p>
 */
public class Main {

    /**
     * The application's entry point. No command line arguments are used.
     *
     * @param args The application does not take any command line parameters.
     */
    public static void main(String[] args) {
        RegistryCreator registryCreator = RegistryCreator.getInstance();
        Printer printer = new Printer();
        Controller controller = new Controller(registryCreator, printer);
        controller.addRepairOrderObserver(new RepairOrderView());
        controller.addRepairOrderObserver(new RepairOrderLogger());
        View view = new View(controller);
        view.sampleExecution();
    }
}
