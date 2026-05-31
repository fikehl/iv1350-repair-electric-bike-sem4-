package se.kth.iv1350.repairbike.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DiagnosticReport}.
 */
public class DiagnosticReportTest {
    private List<RepairTask> tasks;
    private DiagnosticReport report;

    @BeforeEach
    public void setUp() {
        tasks = new ArrayList<>();
        tasks.add(new RepairTask("Replace front brake pads", new Amount(450)));
        tasks.add(new RepairTask("Adjust gears", new Amount(200)));
        tasks.add(new RepairTask("Replace inner tube", new Amount(150)));
        report = new DiagnosticReport("Brakes worn, gears slip, rear tire flat.", tasks);
    }

    @AfterEach
    public void tearDown() {
        tasks = null;
        report = null;
    }

    @Test
    public void testGetTotalCostSumsAllTaskCosts() {
        Amount expected = new Amount(450 + 200 + 150);
        Amount actual = report.getTotalCost();
        assertEquals(expected, actual,
                "getTotalCost did not return the sum of all repair task costs.");
    }

    @Test
    public void testGetTotalCostForEmptyReportIsZero() {
        DiagnosticReport emptyReport = new DiagnosticReport("Nothing wrong.", new ArrayList<>());
        Amount expected = new Amount(0);
        assertEquals(expected, emptyReport.getTotalCost(),
                "getTotalCost was not zero for a report with no repair tasks.");
    }

    @Test
    public void testConstructorTakesDefensiveCopyOfTasks() {
        tasks.add(new RepairTask("Extra task added after construction", new Amount(9999)));
        Amount expected = new Amount(450 + 200 + 150);
        assertEquals(expected, report.getTotalCost(),
                "DiagnosticReport was affected by changes to the original task list,"
                + " but the constructor must take a defensive copy.");
    }

    @Test
    public void testGetProposedTasksIsUnmodifiable() {
        List<RepairTask> returned = report.getProposedTasks();
        assertThrows(UnsupportedOperationException.class,
                () -> returned.add(new RepairTask("Sneaky", new Amount(1))),
                "getProposedTasks returned a list that allowed modification.");
    }

    @Test
    public void testGetDescriptionReturnsConstructorArgument() {
        String description = "All good, just needs a wash.";
        DiagnosticReport otherReport = new DiagnosticReport(description, new ArrayList<>());
        assertEquals(description, otherReport.getDescription(),
                "getDescription did not return the description passed to the constructor.");
    }
}
