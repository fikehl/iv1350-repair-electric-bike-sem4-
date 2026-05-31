package se.kth.iv1350.repairbike.model;

import java.time.LocalDate;
import java.time.Month;
import java.util.function.Supplier;
import se.kth.iv1350.repairbike.integration.CustomerDTO;

/**
 * A seasonal discount strategy: a small percentage off the subtotal, but
 * only during the winter months (December, January, February). The current
 * date is determined when the discount is calculated, so the same strategy
 * instance will give different results in different seasons.
 */
public class WinterDiscount implements DiscountStrategy {
    private static final int WINTER_PERCENT = 5;

    private final Supplier<LocalDate> clock;

    /**
     * Creates a new instance that uses the real system clock to determine
     * the current date.
     */
    public WinterDiscount() {
        this(LocalDate::now);
    }

    /**
     * Creates a new instance that uses the specified supplier as a clock.
     * This constructor is package-private because it exists only for unit
     * tests.
     *
     * @param clock A supplier returning the date used to determine whether
     *              the discount should be applied.
     */
    WinterDiscount(Supplier<LocalDate> clock) {
        this.clock = clock;
    }

    @Override
    public Amount calculateDiscount(Amount subtotal, CustomerDTO customer) {
        Month month = clock.get().getMonth();
        if (month != Month.DECEMBER && month != Month.JANUARY
                && month != Month.FEBRUARY) {
            return new Amount(0);
        }
        int discount = subtotal.getValue() * WINTER_PERCENT / 100;
        return new Amount(discount);
    }

    @Override
    public String getDescription() {
        return WINTER_PERCENT + "% winter discount (Dec-Feb)";
    }
}
