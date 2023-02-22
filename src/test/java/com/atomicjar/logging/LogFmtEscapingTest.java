package com.atomicjar.logging;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class LogFmtEscapingTest {

    static String[][] params() {
        return new String[][] {
            { "some/value", "some/value" },
            { "with\nnewline", "\"with\\nnewline\"" },
            { "with\rcarriagereturn", "\"with\\rcarriagereturn\"" },
            { "with\ttab", "\"with\\ttab\"" },
            { "with space", "\"with space\"" },
            { "with\\backslash", "with\\backslash" },
            // from https://github.com/go-logfmt/logfmt/blob/master/encode_internal_test.go#L151
            { "", "" },
            { "v", "v" },
            { " ", "\" \"" },
            { "=", "\"=\"" },
            { "\\", "\\" },
            { "\"", "\"\\\"\"" },
            { "\\\"", "\"\\\\\\\"\"" },
            { "\n", "\"\\n\"" },
            { "\u0000", "\"\\u0000\"" },
            { "\u0010", "\"\\u0010\"" },
            { "\u001f", "\"\\u001F\"" }, // we deviate from go-logfmt here in generating uppercase hex
            { "µ", "µ" },
        };
    }

    @ParameterizedTest
    @MethodSource(value = "params")
    void test(String original, String expected) {
        StringBuilder sb = new StringBuilder();
        LogFmtEscaping.escapeAndQuoteValue(original, sb);
        assertThat(sb).hasToString(expected);
    }
}
