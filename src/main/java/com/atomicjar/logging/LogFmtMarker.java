package com.atomicjar.logging;

import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Simple implementation of {@link Marker}, intended to be used for <i>per-log statement</i>
 * key-value pairs for logfmt formatted output.
 * <br />
 * Usage:
 * <pre>
 *     log.info(
 *         LogFmtMarker.with("some_key", theValue),
 *         "Some parameterless log message for exact searching"
 *     );
 * </pre>
 * <br />
 * This is not intended to be a complete implementation of {@link Marker}.
 */
public class LogFmtMarker implements Marker {

    private final Map<String, Object> data;

    private LogFmtMarker(Map<String,Object> data) {
        this.data = data;
    }


    public static LogFmtMarker with(String key, Object value) {
        var marker = new LogFmtMarker(Collections.emptyMap());
        return value == null ? marker : marker.and(key, value);
    }

    public LogFmtMarker and(String key, Object value) {
        var copy = new HashMap<>(data);
        if (value != null) {
            copy.put(key, value);
        } else {
            copy.remove(key);
        }
        return new LogFmtMarker(copy);
    }

    void forEachData(BiConsumer<String, Object> consumer) {
        data.forEach(consumer);
    }

    public Map<String, Object> getData() {
        return Map.copyOf(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void add(Marker reference) {}

    @Override
    public boolean remove(Marker reference) {
        return false;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean hasReferences() {
        return false;
    }

    @Override
    public Iterator<Marker> iterator() {
        return null;
    }

    @Override
    public boolean contains(Marker other) {
        return false;
    }

    @Override
    public boolean contains(String name) {
        return false;
    }
}
