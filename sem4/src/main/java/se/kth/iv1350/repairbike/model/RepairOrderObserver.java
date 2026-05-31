package se.kth.iv1350.repairbike.model;

/**
 * A listener that is notified whenever a {@link RepairOrder} is updated in
 * any way. Implementations of this interface form the "Observer" role of the
 * Observer design pattern, while {@link RepairOrder} is the corresponding
 * "Subject". Observers are not allowed to call back into the controller or
 * the model; they must only react to the data that is pushed to them through
 * {@link #repairOrderUpdated(RepairOrderDTO)}.
 */
public interface RepairOrderObserver {

    /**
     * Called whenever the observed repair order has been updated. The
     * supplied snapshot contains all data that the observer is allowed to
     * see and is immutable, so observers can store it freely without
     * exposing the internals of the {@link RepairOrder}.
     *
     * @param snapshot An immutable description of the updated repair order.
     */
    void repairOrderUpdated(RepairOrderDTO snapshot);
}
