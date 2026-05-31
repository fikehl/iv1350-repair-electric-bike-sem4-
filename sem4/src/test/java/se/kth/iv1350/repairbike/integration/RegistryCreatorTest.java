package se.kth.iv1350.repairbike.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RegistryCreator}, verifying that the Singleton pattern
 * is correctly implemented: there is at most one instance, and all callers
 * share the same registry references.
 */
public class RegistryCreatorTest {

    @Test
    public void testGetInstanceReturnsSameObjectEveryCall() {
        RegistryCreator first = RegistryCreator.getInstance();
        RegistryCreator second = RegistryCreator.getInstance();
        assertSame(first, second,
                "RegistryCreator.getInstance() returned different instances.");
    }

    @Test
    public void testCustomerRegistryIsShared() {
        CustomerRegistry first = RegistryCreator.getInstance().getCustomerRegistry();
        CustomerRegistry second = RegistryCreator.getInstance().getCustomerRegistry();
        assertSame(first, second,
                "Two calls to getCustomerRegistry returned different registries.");
    }

    @Test
    public void testRepairOrderRegistryIsShared() {
        RepairOrderRegistry first = RegistryCreator.getInstance().getRepairOrderRegistry();
        RepairOrderRegistry second = RegistryCreator.getInstance().getRepairOrderRegistry();
        assertSame(first, second,
                "Two calls to getRepairOrderRegistry returned different registries.");
    }
}
