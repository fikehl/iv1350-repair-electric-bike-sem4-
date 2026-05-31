package se.kth.iv1350.repairbike.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Amount}.
 */
public class AmountTest {
    private Amount oneHundred;
    private Amount twoHundred;

    @BeforeEach
    public void setUp() {
        oneHundred = new Amount(100);
        twoHundred = new Amount(200);
    }

    @AfterEach
    public void tearDown() {
        oneHundred = null;
        twoHundred = null;
    }

    @Test
    public void testPlusReturnsSum() {
        Amount expected = new Amount(300);
        Amount actual = oneHundred.plus(twoHundred);
        assertEquals(expected, actual,
                "plus did not return the sum of the two amounts.");
    }

    @Test
    public void testPlusDoesNotMutateThis() {
        oneHundred.plus(twoHundred);
        assertEquals(100, oneHundred.getValue(),
                "plus mutated the receiver, but Amount must be immutable.");
    }

    @Test
    public void testPlusDoesNotMutateOther() {
        oneHundred.plus(twoHundred);
        assertEquals(200, twoHundred.getValue(),
                "plus mutated the argument, but Amount must be immutable.");
    }

    @Test
    public void testPlusWithZero() {
        Amount zero = new Amount(0);
        Amount result = oneHundred.plus(zero);
        assertEquals(oneHundred, result,
                "Adding zero changed the amount.");
    }

    @Test
    public void testEqualsForSameValueIsTrue() {
        Amount sameValue = new Amount(100);
        assertTrue(oneHundred.equals(sameValue),
                "Two amounts with the same value were not equal.");
    }

    @Test
    public void testEqualsForDifferentValueIsFalse() {
        assertFalse(oneHundred.equals(twoHundred),
                "Two amounts with different values were equal.");
    }

    @Test
    public void testEqualsWithNullIsFalse() {
        assertFalse(oneHundred.equals(null),
                "Amount instance was equal to null.");
    }

    @Test
    public void testEqualsWithOtherTypeIsFalse() {
        assertFalse(oneHundred.equals("100"),
                "Amount was equal to an instance of another type.");
    }

    @Test
    public void testHashCodeIsConsistentWithEquals() {
        Amount sameValue = new Amount(100);
        assertEquals(oneHundred.hashCode(), sameValue.hashCode(),
                "Two equal amounts produced different hash codes.");
    }

    @Test
    public void testToStringContainsValue() {
        String text = oneHundred.toString();
        assertTrue(text.contains("100"),
                "toString did not contain the amount's value.");
    }
}
