# Log Parser - Device Testing Profiling Log Analyzer

A Java 21 application that parses device testing profiling logs and generates performance statistics for subtests. Designed for analyzing hierarchical test execution traces with timing information.

## Overview

This project processes structured profiling logs from device testing equipment (e.g., ATE - Automated Test Equipment) and generates summary statistics for individual subtests. It helps identify performance bottlenecks by filtering subtests that exceed a specified duration threshold.

### Key Features

- ✅ **Efficient Log Parsing** - Regex-based parsing for structured profiling events
- ✅ **Hierarchical Event Processing** - Handles nested BEGIN/END event pairs
- ✅ **Performance Statistics** - Calculates duration and execution counts for subtests
- ✅ **Threshold Filtering** - Identifies slow-running subtests based on configurable duration thresholds
- ✅ **Professional Logging** - SLF4J + Log4j2 for production-grade logging
- ✅ **Executable JAR** - Maven Shade plugin creates fat JAR for easy distribution
- ✅ **Java 21** - Leverages modern Java language features

## Project Structure

```
parcer/
├── src/
│   ├── main/java/com/example/
│   │   ├── LogAnalyser.java              # Main entry point
│   │   ├── model/
│   │   │   ├── ProfilingEvent.java       # Immutable event model
│   │   │   ├── EventType.java            # BEGIN/END event types
│   │   │   └── ActiveStep.java           # Active step tracking
│   │   ├── parser/
│   │   │   ├── ProfilingEventParser.java # Regex-based event parser
│   │   │   └── TimeParser.java           # HH:MM:SS.mmm format parser
│   │   ├── processing/
│   │   │   ├── EventProcessor.java       # Main event processing loop
│   │   │   └── StepAggregator.java       # Aggregates statistics per subtest
│   │   ├── reporting/
│   │   │   └── ReportPrinter.java        # Formatted console output
│   │   ├── source/
│   │   │   ├── ProfilingEventSource.java # Abstract source interface
│   │   │   └── FileProfilingEventSource.java # File-based implementation
│   │   └── stats/
│   │       └── DurationStats.java        # Statistics calculations
│   ├── resources/
│   │   └── log4j2.xml                    # Log4j2 configuration
│   └── test/java/
│       └── com/example/parser/
│           └── ProfilingEventParserTest.java # Parser unit tests
├── event.log.sample                      # Example log file
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## Requirements

- **Java 21** or later
- **Maven 3.6+** for building
- Standard Unix/Linux environment (or Windows with Git Bash)

## Building

```bash
cd parcer
mvn clean package
```

This creates two artifacts:

- `target/parser-1.0-SNAPSHOT.jar` - Original JAR with dependencies
- `target/parser-1.0-SNAPSHOT-shaded.jar` - Executable fat JAR (recommended)

## Usage

### Running from Command Line

```bash
java -jar target/parser-1.0-SNAPSHOT-shaded.jar <logfile> <threshold_ms>
```

**Parameters:**

- `<logfile>` - Path to the profiling event log file
- `<threshold_ms>` - Duration threshold in milliseconds (filters subtests with longer durations)

**Example:**

```bash
java -jar target/parser-1.0-SNAPSHOT-shaded.jar event.log.sample 200
```

This parses `event.log.sample` and reports all SUBTEST operations that exceeded 200ms.

### Sample Output

```
=== SUBTEST SUMMARY ===
Subtest: check connection
  Count: 3
  Total Duration: 900ms
  Average Duration: 300ms

Subtest: test digital logic
  Count: 3
  Total Duration: 1500ms
  Average Duration: 500ms

Subtest: measure leakage current
  Count: 3
  Total Duration: 1200ms
  Average Duration: 400ms
```

## Log File Format

The parser expects log files with this format:

```
#PE[HH:MM:SS.mmm] : BEGIN <EVENT_NAME> [optional_qualifier]
#PE[HH:MM:SS.mmm] : END <EVENT_NAME>
```

**Example events from `event.log.sample`:**

```
#PE[14:94:31.300] : BEGIN INSERTION "device #10"
#PE[14:31:01.300] : BEGIN DEVICE_TEST.SUBTEST "check connection"
#PE[14:32:01.600] : END DEVICE_TEST.SUBTEST
#PE[14:34:31.300] : BEGIN INSERTION "device #13"
#PE[14:34:33.400] : END INSERTION
```

**Supported Event Characteristics:**

- Timestamps in `HH:MM:SS.mmm` format (hours:minutes:seconds.milliseconds)
- Event names with dot notation (e.g., `DEVICE_TEST.SUBTEST`)
- Optional quoted qualifiers (e.g., device numbers, test descriptions)
- BEGIN/END pairs track nested operations
- Currently filters only SUBTEST operations (see Known Issues below)

## Architecture & Design Patterns

### Clean Separation of Concerns

- **Parser** - Converts raw strings → structured `ProfilingEvent` objects
- **Source** - Abstracts log file reading (can be extended for other sources)
- **Processor** - Main event loop that processes and routes events
- **Aggregator** - Accumulates statistics per subtest
- **Reporter** - Formats and outputs results

### Immutable Model

- `ProfilingEvent` is immutable for thread-safety and clarity
- Uses Optional for nullable qualifiers

### Processing Pipeline

```
FileProfilingEventSource
    ↓ (provides ProfilingEvent stream)
EventProcessor
    ↓ (routes each event)
StepAggregator
    ↓ (accumulates statistics)
ReportPrinter
    ↓ (formatted output)
```

## Configuration

### Logging

Log4j2 is configured via `src/main/resources/log4j2.xml`. By default, logs go to console. Modify this file to:

- Change log level (DEBUG, INFO, WARN, ERROR)
- Add file appenders
- Adjust formatting

### Hardcoded Filters

Currently, the application has some hardcoded configuration (see Known Issues):

1. **SUBTEST Filter** - Only events containing "SUBTEST" are aggregated
2. **Threshold Pattern** - Log format is regex-based and cannot be modified at runtime
3. **Time Format** - Expects `HH:MM:SS.mmm` format exclusively

## Testing

Run unit tests:

```bash
mvn test
```

The project includes basic parsing tests in `ProfilingEventParserTest.java`. These validate:

- Correct parsing of BEGIN events with timestamps and qualifiers
- Correct parsing of END events
- Handling of qualified and unqualified events

## Known Issues & Technical Debt

### ⚠️ Code Quality Issues

1. **Typo: `treshHoldValue` → `thresholdValue`**
   - Found in `StepAggregator.java` and `DurationStats.java`
   - Recommendation: Rename for consistency

2. **Incomplete Test** - `ProfilingEventParserTest.testParceFailed()` is empty
   - Either implement the test or remove the placeholder

3. **Suboptimal Null Handling** - `EventProcessor.java` line 22
   - Current: `if(event.getQualifier().orElse(null) != null)`
   - Better: Use `Optional.ifPresent()` instead

### 🔴 Limitations

1. **Hardcoded Step Filter** - Only processes events with "SUBTEST" in the name
   - Cannot aggregate other event types without code changes
   - Could be parameterized via configuration

2. **Static Threshold Constant** - `DurationStats.THRESHOLD` is hardcoded
   - Currently uses command-line argument, but inconsistently applied
   - Consider centralizing threshold logic

3. **Fixed Log Format** - Regex pattern in `ProfilingEventParser` is hardcoded
   - Could be externalized to properties file for flexibility

## Future Enhancements

- [ ] Externalize log format patterns to configuration file
- [ ] Support multiple event filters via command-line options
- [ ] Add HTML/JSON report output formats
- [ ] Implement streaming parser for large log files
- [ ] Add filtering/search capabilities by event name or qualifier
- [ ] Support for distributed event sources (network, cloud storage)
- [ ] Performance metrics (parse rate, memory usage)

## Dependencies

- **SLF4J 2.0.7** - Logging facade
- **Log4j2 2.23.1** - Logging implementation
- **JUnit 5** - Testing framework (test scope)

## License

[Add license information here]

## Contributing

[Add contribution guidelines here]

## Support & Issues

For issues, feature requests, or questions:

- Review the code comments in `review.md` for known limitations
- Check the Known Issues section above
- Review unit tests for usage examples

---

**Last Updated:** May 2026  
**Java Version:** 21  
**Build Tool:** Maven 3.6+
