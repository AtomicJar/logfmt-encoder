# logfmt-encoder

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.atomicjar/logfmt-encoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.atomicjar/logfmt-encoder)

A Java library that follows the logfmt format, a logging format designed to be both human-readable and machine-parseable, 
making it easy to understand and analyze log data.

## Usage

First, register the encoder in your logback.xml:

```xml
<configuration>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="com.atomicjar.logging.LogFmtLayout">
            </layout>
        </encoder>
    </appender>

    <root level="info">
        <appender-ref ref="stdout"/>
    </root>
</configuration>
```

In your code, use the `LogFmtMarker` to add key-value pairs to your log message

```java
LogFmtMarker logFmtMarker = LogFmtMarker
            .with("event", "release")
            .and("artifact", "logfmt-encoder")
            .and("version", "1.0.0");
logger.info(logFmtMarker, "Release is produced");
```
