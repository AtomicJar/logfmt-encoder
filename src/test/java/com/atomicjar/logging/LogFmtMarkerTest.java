package com.atomicjar.logging;

import org.junit.jupiter.api.Test;

public class LogFmtMarkerTest {
    @Test
    public void testImmutability() {
        var orig = LogFmtMarker.with("a", "b");
        orig.and("c", "d");
        assert orig.getData().size() == 1;
    }
}
