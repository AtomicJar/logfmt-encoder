package com.atomicjar.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.LayoutBase;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Logback {@link ch.qos.logback.core.Layout} which formats log events in logfmt format.
 */
public class LogFmtLayout extends LayoutBase<ILoggingEvent> {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();

        String ts = TIMESTAMP_FORMAT.format(Instant.ofEpochMilli(event.getTimeStamp()).atZone(ZoneId.systemDefault()));
        add(sb, "time", ts);

        add(sb, "level", event.getLevel().toString().toLowerCase());

        add(sb, "msg", event.getFormattedMessage());

        add(sb, "logger", event.getLoggerName());

        if (event.getMarker() instanceof LogFmtMarker logFmtMarker) {
            logFmtMarker.forEachData((s, o) -> add(sb, s, o));
        }

        if (event.getThrowableProxy() != null) {
            add(sb, "exception_class", event.getThrowableProxy().getClassName());
            add(sb, "exception_msg", event.getThrowableProxy().getMessage());
            String throwableAsString = ThrowableProxyUtil.asString(event.getThrowableProxy());

            // since ThrowableProxyUtil uses platform dependent line separator, we have to remove
            // carriage-return, assuming it was only added through logback
            throwableAsString = throwableAsString.replace("\r", "");
            add(sb, "error", throwableAsString);
        }

        sb.append('\n');

        return sb.toString();
    }

    private void add(StringBuilder sb, String key, Object value) {
        if (sb.length() > 0) {
            sb.append(' ');
        }

        sb.append(key).append("=");

        String valueAsString;
        if (value instanceof Duration d) {
            // Reformat in a Golang compatible way. e.g. PT2H40S -> 2H40S -> 2h40s.
            // Duration is formatted using hours, minutes and seconds. Larger/smaller amounts will be shown using the
            // nearest of these units, which is permissible according to the Golang ParseDuration spec.
            valueAsString = d.toString().substring(2).toLowerCase();
        } else {
            valueAsString = String.valueOf(value);
        }

        LogFmtEscaping.escapeAndQuoteValue(valueAsString, sb);
    }
}
