package se.kth.iv1350.repairbike.model;

import se.kth.iv1350.repairbike.integration.CustomerDTO;

/**
 * The trivial discount strategy: no discount is ever applied. This is the
 * default strategy used when no other strategy is supplied to a
 * {@link RepairOrder}.
 */
public class NoDiscount implements DiscountStrategy {

    /**
     * Creates a new instance.
     */
    public NoDiscount() {
    }

    /**
     * Always returns a discount of zero, independent of subtotal and
     * customer.
     */
    @Override
    public Amount calculateDiscount(Amount subtotal, CustomerDTO customer) {
        return new Amount(0);
    }

    @Override
    public String getDescription() {
        return "No discount";
    }
}
