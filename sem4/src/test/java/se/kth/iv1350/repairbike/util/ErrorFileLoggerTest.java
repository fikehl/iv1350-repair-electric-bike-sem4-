package se.kth.iv1350.repairbike.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ErrorFileLogger}.
 */
public class ErrorFileLoggerTest {
    private Path tempLog;

    @BeforeEach
    public void setUp() throws IOException {
        tempLog = Files.createTempFile("error-file-logger-test", ".log");
        Files.deleteIfExists(tempLog);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempLog);
    }

    @Test
    public void testLogExceptionWritesMessageAndStackTrace() throws IOException {
        ErrorFileLogger logger = new ErrorFileLogger(tempLog);
        Exception cause = new IllegalStateException("simulated failure");
        logger.logException("Something went wrong", cause);

        assertTrue(Files.exists(tempLog),
                "ErrorFileLogger did not create the log file.");
        String content = Files.readString(tempLog);
        assertTrue(content.contains("Something went wrong"),
                "Log entry did not include the message.");
        assertTrue(content.contains("IllegalStateException"),
                "Log entry did not include the exception class name.");
        assertTrue(content.contains("simulated failure"),
                "Log entry did not include the exception's own message.");
    }

    @Test
    public void testLogExceptionAppendsAndDoesNotOverwrite() throws IOException {
        ErrorFileLogger logger = new ErrorFileLogger(tempLog);
        logger.logException("first entry", new IllegalStateException("first"));
        logger.logException("second entry", new IllegalStateException("second"));

        String content = Files.readString(tempLog);
        assertTrue(content.contains("first entry"),
                "Log file did not contain the first entry.");
        assertTrue(content.contains("second entry"),
                "Log file did not contain the second entry, so the second call overwrote the first.");
    }
}
