package com.atomicjar.logging;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.UnicodeEscaper;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * There doesn't appear to be a canonical logfmt spec. This implementation follows patterns established in other
 * libraries - see links below.
 */
class LogFmtEscaping {

    // Based on https://pkg.go.dev/github.com/kr/logfmt?utm_source=godoc
    private static final Pattern QUOTABLE_CHARACTERS = Pattern.compile("[\\u0000-\\u0020=\"]");

    // Based on https://github.com/go-logfmt/logfmt/blob/master/jsonstring.go
    private static final CharSequenceTranslator ESCAPE_LOGFMT = new AggregateTranslator(
        new LookupTranslator(
            Map.ofEntries(
                Map.entry("\"", "\\\""),
                Map.entry("\\", "\\\\"),
                Map.entry("\n", "\\n"),
                Map.entry("\r", "\\r"),
                Map.entry("\t", "\\t")
            )
        ),
        UnicodeEscaper.below(0x20)
    );

    static void escapeAndQuoteValue(String original, StringBuilder sb) {
        if (QUOTABLE_CHARACTERS.matcher(original).find()) {
            sb.append('"');
            sb.append(ESCAPE_LOGFMT.translate(original));
            sb.append('"');
        } else {
            sb.append(original);
        }
    }
}
