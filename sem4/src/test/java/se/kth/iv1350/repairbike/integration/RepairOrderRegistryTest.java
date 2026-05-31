package se.kth.iv1350.repairbike.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.kth.iv1350.repairbike.model.RepairOrder;

/**
 * Unit tests for {@link RepairOrderRegistry}. The tests construct fresh
 * {@link RepairOrderRegistry} instances directly (the constructor is
 * package-private and accessible to this test, which lives in the same
 * package). This keeps the singleton {@link RegistryCreator} out of the way
 * and gives every test its own clean state.
 */
public class RepairOrderRegistryTest {
    private RepairOrderRegistry registry;
    private CustomerDTO customer;

    @BeforeEach
    public void setUp() {
        registry = new RepairOrderRegistry();
        customer = new CustomerDTO("Test Person", "0712345678",
                                   "test@example.com",
                                   new BikeDTO("TestBrand", "TestModel", "T-001"));
    }

    @AfterEach
    public void tearDown() {
        registry = null;
        customer = null;
    }

    @Test
    public void testNewRegistryIsEmpty() {
        assertEquals(0, registry.numberOfRepairOrders(),
                "A new RepairOrderRegistry was not empty.");
    }

    @Test
    public void testSavingRepairOrderIncreasesCount() {
        RepairOrder repairOrder = new RepairOrder(
                registry.nextRepairOrderId(), customer, "Squeaking brakes.");
        registry.saveRepairOrder(repairOrder);
        assertEquals(1, registry.numberOfRepairOrders(),
                "Number of repair orders did not increase after saving one.");
    }

    @Test
    public void testSavingSameOrderTwiceDoesNotDuplicate() {
        RepairOrder repairOrder = new RepairOrder(
                registry.nextRepairOrderId(), customer, "Squeaking brakes.");
        registry.saveRepairOrder(repairOrder);
        registry.saveRepairOrder(repairOrder);
        assertEquals(1, registry.numberOfRepairOrders(),
                "Saving the same repair order twice produced a duplicate entry.");
    }

    @Test
    public void testNextRepairOrderIdProducesUniqueIds() {
        int firstId = registry.nextRepairOrderId();
        int secondId = registry.nextRepairOrderId();
        assertNotEquals(firstId, secondId,
                "nextRepairOrderId returned the same id twice.");
    }

    @Test
    public void testNextRepairOrderIdIsMonotonicallyIncreasing() {
        int firstId = registry.nextRepairOrderId();
        int secondId = registry.nextRepairOrderId();
        int thirdId = registry.nextRepairOrderId();
        assertTrue(secondId > firstId && thirdId > secondId,
                "nextRepairOrderId did not return monotonically increasing ids.");
    }
}
