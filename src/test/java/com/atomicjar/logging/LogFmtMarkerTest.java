package com.atomicjar.logging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogFmtMarkerTest {
    @Test
    public void testImmutability() {
        var orig = LogFmtMarker.with("a", "b");
        orig.and("c", "d");
        assertThat(orig.getData()).hasSize(1);
    }

    @Test
    public void testWithNullValues() {
        LogFmtMarker.with("a", null);
    }

    @Test
    public void testAndNullValues() {
        LogFmtMarker.with("a", "b").and("c", null);
    }
}
