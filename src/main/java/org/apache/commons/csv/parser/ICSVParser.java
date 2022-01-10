package org.apache.commons.csv.parser;

import org.apache.commons.csv.record.CSVRecord;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface ICSVParser extends Iterable<CSVRecord>, Closeable {
    long getCurrentLineNumber();

    Map<String, Integer> getHeaderMap();

    Map<String, Integer> getHeaderMapRaw();

    List<String> getHeaderNames();

    long getRecordNumber();

    List<CSVRecord> getRecords() throws IOException;

    CSVRecord nextRecord() throws IOException;

    Stream<CSVRecord> stream();
}
