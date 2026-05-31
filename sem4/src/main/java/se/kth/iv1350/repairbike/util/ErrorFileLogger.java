package se.kth.iv1350.repairbike.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes one log entry per call to a plain-text log file. Each entry is
 * prefixed with a timestamp and ends with the full stack trace of the
 * underlying exception. The logger appends to the file, so existing entries
 * are never overwritten.
 *
 * <p>This logger is intentionally simple. It is inspired by the
 * {@code FileLogger} example in listing 9.1 of the course textbook but does
 * not copy code from it. Any I/O problem while writing to the log is caught
 * and printed to {@code System.err}, so that a missing log file never
 * prevents the program from notifying the user about the original error.</p>
 */
public class ErrorFileLogger {
    private static final String DEFAULT_LOG_FILE_NAME = "repair-bike-error-log.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logFile;

    /**
     * Creates a new instance writing to the default log file
     * ({@code repair-bike-error-log.txt} in the current working directory).
     */
    public ErrorFileLogger() {
        this(Path.of(DEFAULT_LOG_FILE_NAME));
    }

    /**
     * Creates a new instance writing to the specified file. The file is
     * created if it does not exist; existing content is preserved.
     *
     * @param logFile The file that error reports shall be written to.
     */
    public ErrorFileLogger(Path logFile) {
        this.logFile = logFile;
    }

    /**
     * Writes the specified message and the stack trace of the specified
     * exception to the log file. A timestamp is prepended automatically.
     *
     * @param message A short message describing what the program was doing
     *                when the exception occurred.
     * @param cause   The exception that was caught.
     */
    public void logException(String message, Throwable cause) {
        StringBuilder entry = new StringBuilder();
        entry.append(LocalDateTime.now().format(TIMESTAMP_FORMAT));
        entry.append("  ");
        entry.append(message);
        entry.append(System.lineSeparator());
        if (cause != null) {
            entry.append(stackTraceOf(cause));
            entry.append(System.lineSeparator());
        }
        writeToFile(entry.toString());
    }

    private String stackTraceOf(Throwable cause) {
        StringWriter stringWriter = new StringWriter();
        cause.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private void writeToFile(String text) {
        try {
            Files.writeString(logFile, text,
                              StandardOpenOption.CREATE,
                              StandardOpenOption.APPEND);
        } catch (IOException ioException) {
            System.err.println("Could not write to error log "
                               + logFile.toAbsolutePath() + ": "
                               + ioException.getMessage());
        }
    }

    /**
     * @return The path of the file that this logger writes to.
     */
    public Path getLogFile() {
        return logFile;
    }
}
