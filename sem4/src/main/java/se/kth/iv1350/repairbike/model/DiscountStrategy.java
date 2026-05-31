package se.kth.iv1350.repairbike.model;

import se.kth.iv1350.repairbike.integration.CustomerDTO;

/**
 * The {@code Strategy} role in the Strategy design pattern, used here to
 * encapsulate different algorithms for calculating customer discounts on a
 * repair order. The {@link RepairOrder} holds a reference to one
 * {@link DiscountStrategy} and delegates the calculation to it, which lets us
 * vary the discount rule (loyalty, seasonal, none, ...) without changing the
 * {@link RepairOrder} itself.
 */
public interface DiscountStrategy {

    /**
     * Returns the discount that shall be applied to the specified subtotal,
     * for the specified customer.
     *
     * @param subtotal The total cost of the proposed repair tasks before
     *                 any discount is applied.
     * @param customer The customer that owns the repair order.
     * @return The amount that shall be subtracted from {@code subtotal} to
     *         obtain the discounted total. The returned amount must never
     *         exceed {@code subtotal}.
     */
    Amount calculateDiscount(Amount subtotal, CustomerDTO customer);

    /**
     * @return A short, human-readable description of this discount strategy,
     *         intended to be printed on the repair order.
     */
    String getDescription();
}
