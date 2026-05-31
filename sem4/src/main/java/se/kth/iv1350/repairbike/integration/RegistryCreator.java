package se.kth.iv1350.repairbike.integration;

/**
 * This class is responsible for instantiating all registries in the
 * integration layer. The same instances are shared by every caller asking for
 * a registry.
 *
 * <p>This class is a <strong>Singleton</strong>: there is exactly one
 * instance during the lifetime of the program, obtained through
 * {@link #getInstance()}. The motivation for the pattern is that the
 * registries represent shared external resources (one customer database, one
 * repair-order database). Multiple instances would each maintain their own
 * in-memory state, which would silently introduce inconsistencies between
 * different parts of the program. The Singleton ensures all callers see the
 * same registries without passing them through every constructor.</p>
 *
 * <p>The known cost of the Singleton pattern is that tests cannot easily get
 * a clean registry state for each test method. To compensate, the static
 * factory {@link #createForTests()} is exposed; it bypasses the Singleton
 * and returns an isolated {@link RegistryCreator} that production code never
 * uses.</p>
 */
public class RegistryCreator {
    private static volatile RegistryCreator instance;

    private final CustomerRegistry customerRegistry;
    private final RepairOrderRegistry repairOrderRegistry;

    private RegistryCreator() {
        this.customerRegistry = new CustomerRegistry();
        this.repairOrderRegistry = new RepairOrderRegistry();
    }

    /**
     * Returns the single instance of {@link RegistryCreator}. The instance is
     * created lazily the first time this method is called, in a thread-safe
     * way using double-checked locking.
     *
     * @return The single {@link RegistryCreator} instance.
     */
    public static RegistryCreator getInstance() {
        if (instance == null) {
            synchronized (RegistryCreator.class) {
                if (instance == null) {
                    instance = new RegistryCreator();
                }
            }
        }
        return instance;
    }

    /**
     * Creates an isolated {@link RegistryCreator} that does <em>not</em>
     * participate in the Singleton. Intended only for unit tests that need a
     * fresh registry state per test method.
     *
     * @return A new {@link RegistryCreator} with its own
     *         {@link CustomerRegistry} and {@link RepairOrderRegistry}.
     */
    public static RegistryCreator createForTests() {
        return new RegistryCreator();
    }

    /**
     * @return The single instance of the customer registry.
     */
    public CustomerRegistry getCustomerRegistry() {
        return customerRegistry;
    }

    /**
     * @return The single instance of the repair order registry.
     */
    public RepairOrderRegistry getRepairOrderRegistry() {
        return repairOrderRegistry;
    }
}
