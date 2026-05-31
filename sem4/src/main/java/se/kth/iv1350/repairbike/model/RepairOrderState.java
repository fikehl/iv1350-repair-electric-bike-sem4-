package se.kth.iv1350.repairbike.model;

/**
 * Defines all possible states of a {@link RepairOrder} during its life cycle.
 * The states {@code COMPLETED} and {@code PAID} are defined for completeness
 * but are not reachable in this version of the program, since only the basic
 * flow up to acceptance is implemented.
 */
public enum RepairOrderState {
    /** No diagnostic report or repair tasks have been added yet. */
    NEWLY_CREATED,

    /** A technician has entered diagnostic report and proposed repair tasks. */
    READY_FOR_APPROVAL,

    /** The customer did not want the proposed repair tasks. */
    REJECTED,

    /** The customer accepted the proposed repair tasks. */
    ACCEPTED,

    /** The reparation has been done, but the customer has not paid yet. */
    COMPLETED,

    /** The customer has paid for the reparation. */
    PAID
}
