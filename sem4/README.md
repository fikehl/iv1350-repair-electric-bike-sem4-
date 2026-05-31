# Repair Electric Bike — Seminar 4 (IV1350)

Java implementation of the *Repair Electric Bike* use case for the KTH course
**IV1350 Object-Oriented Design**. This repository is the deliverable for
**Seminar 4, Exceptions and Design Patterns**, and extends the seminar 3
solution with exception handling and Gang-of-Four design patterns.

## What is new in seminar 4

* **Task 1 — exception handling**: two new exceptions, a file-based error
  logger and informative user messages in the view.
  * `CustomerNotFoundException` (checked) — thrown when the customer
    registry cannot find a matching phone number.
  * `DatabaseFailureException` (unchecked) — thrown by the integration
    layer when the simulated database server is unreachable. The failure is
    simulated by always throwing for the hard-coded phone number
    `0700000000`.
  * `ErrorFileLogger` — appends timestamped stack traces to
    `repair-bike-error-log.txt` so developers can diagnose the failure.
* **Task 2a — Observer pattern**: `RepairOrder` is now an observable. Two
  observer implementations are wired up in startup:
  * `RepairOrderView` — pushes every update to `System.out`. Replaces the
    "technician asks the system for the repair order" step in the seminar 1
    scenario.
  * `RepairOrderLogger` — appends every update to `repair-order-log.txt`,
    inspired by but not copied from the textbook's `FileLogger` (listing
    9.1).
* **Task 2b — two additional GoF patterns**:
  * **Singleton** — `RegistryCreator` now has a private constructor and a
    `getInstance()` method (with double-checked locking).
  * **Strategy** — `DiscountStrategy` with concrete implementations
    `NoDiscount`, `LoyaltyDiscount` and `WinterDiscount`. The strategy can
    be replaced at runtime via `Controller.applyDiscountStrategy`.

## Project layout

```
src/
  main/java/se/kth/iv1350/repairbike/
    startup/      Main.java
    view/         View.java, RepairOrderView.java, RepairOrderLogger.java
    controller/   Controller.java
    model/        RepairOrder.java, RepairOrderObserver.java,
                  RepairOrderDTO.java, DiagnosticReport.java,
                  RepairTask.java, Amount.java, RepairOrderState.java,
                  DiscountStrategy.java, NoDiscount.java,
                  LoyaltyDiscount.java, WinterDiscount.java
    integration/  CustomerDTO.java, BikeDTO.java,
                  CustomerRegistry.java, RepairOrderRegistry.java,
                  RegistryCreator.java, Printer.java,
                  CustomerNotFoundException.java,
                  DatabaseFailureException.java
    util/         ErrorFileLogger.java
  test/java/se/kth/iv1350/repairbike/   (matching test packages)
report/
    seminar4-report.pdf   IMRaD report for seminar 4.
    sample-run.txt         Captured System.out output of one full run.
pom.xml                    Maven build (Java 17 + JUnit 5).
```

## Building and running

Requires Java 17 (or later) and Apache Maven.

```bash
# Run the program
mvn -q exec:java

# Run the unit tests
mvn -q test
```

## What is implemented

The basic flow of the use case from seminar 1, plus the two alternative
flows that motivate the new exception handling:

1. The receptionist looks up a customer by phone number.
2. The customer's problem description is registered, which creates a new
   repair order in state `NEWLY_CREATED`.
3. The technician registers the diagnostic report and the proposed repair
   tasks, which moves the order to state `READY_FOR_APPROVAL` and notifies
   all observers.
4. The receptionist optionally selects a discount strategy.
5. The receptionist registers that the customer accepts the proposed tasks,
   which moves the order to state `ACCEPTED`, notifies observers and
   triggers the printer.
6. *Alternative flow 5a* — unknown phone number: `CustomerNotFoundException`
   is caught in the view and reported to the user.
7. *Task 1b — simulated database failure* (not in the seminar 1
   specification): `DatabaseFailureException` is caught in the view,
   reported to the user and logged to file for the developers.

## Sample run

See `report/sample-run.txt` for the complete output of one full execution
covering both the basic flow and the two error scenarios.
