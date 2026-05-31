package se.kth.iv1350.repairbike.model;

/**
 * Represents an amount of money, used for example for the cost of a repair
 * task. Instances are immutable, every arithmetic operation returns a new
 * instance.
 */
public final class Amount {
    private final int value;

    /**
     * Creates a new instance representing the specified amount.
     *
     * @param value The amount represented by the newly created instance.
     */
    public Amount(int value) {
        this.value = value;
    }

    /**
     * Adds the specified amount to this amount and returns a new instance with
     * the result. Neither this object nor the specified object is changed.
     *
     * @param other The amount to add.
     * @return A new {@link Amount} instance representing the sum.
     */
    public Amount plus(Amount other) {
        return new Amount(this.value + other.value);
    }

    /**
     * @return The integer value of this amount.
     */
    public int getValue() {
        return value;
    }

    /**
     * Two {@link Amount} instances are equal when they represent the same
     * value.
     *
     * @param other The object to compare to.
     * @return {@code true} if {@code other} is an {@link Amount} with the same
     *         value, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Amount)) {
            return false;
        }
        Amount otherAmount = (Amount) other;
        return value == otherAmount.value;
    }

    /**
     * @return A hash code consistent with {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    /**
     * @return A textual representation of this amount, on the form "value SEK".
     */
    @Override
    public String toString() {
        return value + " SEK";
    }
}
