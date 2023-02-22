package com.atomicjar.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class LogFmtLayoutTest {

    private LoggingEvent dummyEvent;

    private LogFmtLayout layout;

    @BeforeEach
    void setUp() {
        dummyEvent = createDummyEvent("this is the message", new Object[] {});

        layout = new LogFmtLayout();
    }

    private LoggingEvent createDummyEvent(String message, Object[] argArray) {
        Logger logger = (Logger) LoggerFactory.getLogger("com.some.ClassName");

        return new LoggingEvent("com.some.ClassName", logger, Level.INFO, message, null, argArray);
    }

    @Test
    void testSimpleLogEvent() {
        String formattedEvent = layout.doLayout(dummyEvent);

        assertThat(formattedEvent).startsWith("time=");
        // ignore the timestamp part since time and zone may vary
        assertThat(formattedEvent).contains(" level=info msg=\"this is the message\" logger=com.some.ClassName");
        assertThat(formattedEvent).endsWith("\n");
    }

    @Test
    void testLogEventWithStandardPlaceholders() {
        dummyEvent = createDummyEvent("Here: {}", new Object[] { "foo" });
        String formattedEvent = layout.doLayout(dummyEvent);

        assertThat(formattedEvent).contains("msg=\"Here: foo\"");
    }

    @Test
    void testLogEventWithMarkers() {
        dummyEvent.setMarker(LogFmtMarker.with("key1", "value1").and("key2", "value2 requiring quotes"));
        String formattedEvent = layout.doLayout(dummyEvent);

        assertThat(formattedEvent).contains("key1=value1 key2=\"value2 requiring quotes\"");
    }

    @Test
    void testLogEventWithException() {
        dummyEvent.setThrowableProxy(new ThrowableProxy(new RuntimeException("This is an exception")));
        String formattedEvent = layout.doLayout(dummyEvent);

        assertThat(formattedEvent).contains(" level=info msg=\"this is the message\" logger=com.some.ClassName");
        assertThat(formattedEvent)
            .contains(
                "error=\"java.lang.RuntimeException: This is an exception\\n\\tat com.atomicjar.logging.LogFmtLayoutTest."
            );
        assertThat(formattedEvent).contains("exception_class=java.lang.RuntimeException");
        assertThat(formattedEvent).contains("exception_msg=\"This is an exception\"");
    }

    @Test
    void testDurationFormatting() {
        dummyEvent.setMarker(
            LogFmtMarker
                .with("duration_s", Duration.ofSeconds(30))
                .and("duration_mixed", Duration.ofHours(2).plus(5, ChronoUnit.SECONDS))
                .and("duration_millis", Duration.ofMillis(2))
                .and("duration_days", Duration.ofDays(2))
                .and("duration_negative", Duration.ofSeconds(-5))
        );
        String formattedEvent = layout.doLayout(dummyEvent);

        assertThat(formattedEvent).contains("duration_s=30s");
        assertThat(formattedEvent).contains("duration_mixed=2h5s");
        assertThat(formattedEvent).contains("duration_millis=0.002s"); // fractional amounts are permitted
        assertThat(formattedEvent).contains("duration_days=48h");
        assertThat(formattedEvent).contains("duration_negative=-5s");
    }
}
