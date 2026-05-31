package se.kth.iv1350.repairbike.integration;

import java.util.ArrayList;
import java.util.List;
import se.kth.iv1350.repairbike.model.RepairOrder;

/**
 * Contains all calls to the data store with repair orders. In this version of
 * the program no real database is used, instead the repair orders are kept in
 * memory.
 */
public class RepairOrderRegistry {
    private final List<RepairOrder> repairOrders = new ArrayList<>();
    private int nextRepairOrderId = 1;

    /**
     * Creates a new instance.
     */
    RepairOrderRegistry() {
    }

    /**
     * Stores the specified repair order in the registry. Repair orders are
     * never deleted; if the same order is saved again, the registry's copy is
     * simply updated.
     *
     * @param repairOrder The repair order to store.
     */
    public void saveRepairOrder(RepairOrder repairOrder) {
        if (!repairOrders.contains(repairOrder)) {
            repairOrders.add(repairOrder);
        }
    }

    /**
     * Returns the next unused repair order id and reserves it. Calling this
     * method twice will never return the same id.
     *
     * @return The next unused repair order id.
     */
    public int nextRepairOrderId() {
        int reservedId = nextRepairOrderId;
        nextRepairOrderId++;
        return reservedId;
    }

    /**
     * @return The number of repair orders currently stored in the registry.
     */
    public int numberOfRepairOrders() {
        return repairOrders.size();
    }
}
