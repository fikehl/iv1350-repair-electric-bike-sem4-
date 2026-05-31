package se.kth.iv1350.repairbike.model;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.integration.BikeDTO;
import se.kth.iv1350.repairbike.integration.CustomerDTO;

/**
 * Unit tests for the implementations of {@link DiscountStrategy}.
 */
public class DiscountStrategyTest {
    private static final CustomerDTO RETURNING_CUSTOMER = new CustomerDTO(
            "Anna Andersson", "0701112233", "anna@example.com",
            new BikeDTO("Crescent", "Elina E8", "CR-E8-00417"));
    private static final CustomerDTO NEW_CUSTOMER = new CustomerDTO(
            "New Person", "0719999999", "new@example.com",
            new BikeDTO("Cube", "Reaction Hybrid", "CB-RH-90000"));

    @Test
    public void testNoDiscountReturnsZero() {
        DiscountStrategy strategy = new NoDiscount();
        Amount discount = strategy.calculateDiscount(new Amount(1000), RETURNING_CUSTOMER);
        assertEquals(new Amount(0), discount,
                "NoDiscount should never produce a non-zero discount.");
    }

    @Test
    public void testLoyaltyDiscountForReturningCustomerIsTenPercent() {
        DiscountStrategy strategy = new LoyaltyDiscount(
                Set.of(RETURNING_CUSTOMER.getPhoneNumber()));
        Amount discount = strategy.calculateDiscount(new Amount(1000), RETURNING_CUSTOMER);
        assertEquals(new Amount(100), discount,
                "Loyalty discount for a returning customer should be 10% of subtotal.");
    }

    @Test
    public void testLoyaltyDiscountForUnknownCustomerIsZero() {
        DiscountStrategy strategy = new LoyaltyDiscount(
                Set.of(RETURNING_CUSTOMER.getPhoneNumber()));
        Amount discount = strategy.calculateDiscount(new Amount(1000), NEW_CUSTOMER);
        assertEquals(new Amount(0), discount,
                "Loyalty discount should be zero for a customer that is not in the loyalty list.");
    }

    @Test
    public void testWinterDiscountInJanuaryIsFivePercent() {
        DiscountStrategy strategy = new WinterDiscount(
                () -> LocalDate.of(2026, Month.JANUARY, 15));
        Amount discount = strategy.calculateDiscount(new Amount(1000), NEW_CUSTOMER);
        assertEquals(new Amount(50), discount,
                "Winter discount should be 5% of subtotal in January.");
    }

    @Test
    public void testWinterDiscountInJulyIsZero() {
        DiscountStrategy strategy = new WinterDiscount(
                () -> LocalDate.of(2026, Month.JULY, 15));
        Amount discount = strategy.calculateDiscount(new Amount(1000), NEW_CUSTOMER);
        assertEquals(new Amount(0), discount,
                "Winter discount should be zero in July.");
    }

    @Test
    public void testRepairOrderUsesDiscountStrategy() {
        RepairOrder order = new RepairOrder(1, RETURNING_CUSTOMER, "Brakes squeaking.");
        order.addDiagnosticReport(new DiagnosticReport("Worn pads.",
                java.util.List.of(new RepairTask("Replace pads", new Amount(1000)))));
        assertEquals(new Amount(1000), order.toDTO().getTotalCost(),
                "RepairOrder with NoDiscount should have totalCost == subtotal.");

        order.setDiscountStrategy(new LoyaltyDiscount(
                Set.of(RETURNING_CUSTOMER.getPhoneNumber())));
        assertEquals(new Amount(900), order.toDTO().getTotalCost(),
                "RepairOrder did not apply the loyalty discount to the total cost.");
    }

    @Test
    public void testSettingNullDiscountStrategyThrowsAndKeepsState() {
        RepairOrder order = new RepairOrder(1, RETURNING_CUSTOMER, "Brakes squeaking.");
        DiscountStrategy beforeAttempt = order.getDiscountStrategy();
        assertThrows(IllegalArgumentException.class,
                () -> order.setDiscountStrategy(null),
                "setDiscountStrategy(null) must throw IllegalArgumentException.");
        assertSame(beforeAttempt, order.getDiscountStrategy(),
                "Discount strategy must not change when setDiscountStrategy throws.");
    }
}
