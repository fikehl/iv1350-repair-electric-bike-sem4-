package se.kth.iv1350.repairbike.model;

import java.util.Set;
import se.kth.iv1350.repairbike.integration.CustomerDTO;

/**
 * A loyalty discount: returning customers get a fixed percentage off the
 * subtotal. The "is the customer returning" decision is intentionally simple
 * in this version of the program; in a real workshop it would look the
 * customer up in a history database, but here we simply look at the
 * customer's phone number.
 */
public class LoyaltyDiscount implements DiscountStrategy {
    private static final int LOYALTY_PERCENT = 10;

    private final Set<String> returningCustomerPhoneNumbers;

    /**
     * Creates a new instance with a default set of returning customers
     * (Anna and Cecilia from the sample data).
     */
    public LoyaltyDiscount() {
        this(Set.of("0701112233", "0767778899"));
    }

    /**
     * Creates a new instance using the specified set of phone numbers as
     * the list of returning customers.
     *
     * @param returningCustomerPhoneNumbers The phone numbers of customers
     *                                      that qualify for the loyalty
     *                                      discount.
     */
    public LoyaltyDiscount(Set<String> returningCustomerPhoneNumbers) {
        this.returningCustomerPhoneNumbers = Set.copyOf(returningCustomerPhoneNumbers);
    }

    @Override
    public Amount calculateDiscount(Amount subtotal, CustomerDTO customer) {
        if (customer == null
                || !returningCustomerPhoneNumbers.contains(customer.getPhoneNumber())) {
            return new Amount(0);
        }
        int discount = subtotal.getValue() * LOYALTY_PERCENT / 100;
        return new Amount(discount);
    }

    @Override
    public String getDescription() {
        return LOYALTY_PERCENT + "% loyalty discount for returning customers";
    }
}
