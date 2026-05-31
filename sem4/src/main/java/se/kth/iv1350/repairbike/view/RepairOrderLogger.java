package se.kth.iv1350.repairbike.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import se.kth.iv1350.repairbike.model.RepairOrderDTO;
import se.kth.iv1350.repairbike.model.RepairOrderObserver;

/**
 * Observer that appends every update to a repair order to a log file. This is
 * the second implementation of {@link RepairOrderObserver} required by
 * seminar 4 task 2a. The logger is inspired by the textbook's
 * {@code FileLogger} (listing 9.1) but does not copy code from it.
 *
 * <p>Like {@link RepairOrderView}, this observer is only updated through the
 * Observer pattern and never calls back into the controller or the model.</p>
 */
public class RepairOrderLogger implements RepairOrderObserver {
    private static final String DEFAULT_LOG_FILE_NAME = "repair-order-log.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logFile;

    /**
     * Creates a new instance writing to the default log file
     * ({@code repair-order-log.txt} in the current working directory).
     */
    public RepairOrderLogger() {
        this(Path.of(DEFAULT_LOG_FILE_NAME));
    }

    /**
     * Creates a new instance writing to the specified file.
     *
     * @param logFile The file to append log entries to.
     */
    public RepairOrderLogger(Path logFile) {
        this.logFile = logFile;
    }

    @Override
    public void repairOrderUpdated(RepairOrderDTO snapshot) {
        StringBuilder entry = new StringBuilder();
        entry.append("=== ").append(LocalDateTime.now().format(TIMESTAMP_FORMAT))
             .append("  repair order #").append(snapshot.getRepairOrderId())
             .append(" updated (state = ").append(snapshot.getState())
             .append(") ===\n");
        entry.append(snapshot.getPrintout());
        entry.append("\n");
        appendToFile(entry.toString());
    }

    private void appendToFile(String text) {
        try {
            Files.writeString(logFile, text,
                              StandardOpenOption.CREATE,
                              StandardOpenOption.APPEND);
        } catch (IOException ioException) {
            // An observer must never bring down the subject. We print to
            // stderr so the failure is visible, but we never propagate.
            System.err.println("RepairOrderLogger failed to write to "
                               + logFile.toAbsolutePath() + ": "
                               + ioException.getMessage());
        }
    }

    /**
     * @return The file that this logger appends to.
     */
    public Path getLogFile() {
        return logFile;
    }
}
