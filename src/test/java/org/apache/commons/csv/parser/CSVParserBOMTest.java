package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.record.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CSVParserBOMTest {


    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final String UTF_8_NAME = UTF_8.name();

    private BOMInputStream createBOMInputStream(final String resource) throws IOException {
        final URL url = ClassLoader.getSystemClassLoader().getResource(resource);
        return new BOMInputStream(url.openStream());
    }

    @Test
    @Disabled("CSV-107")
    public void testBOM() throws IOException {
        final URL url = ClassLoader.getSystemClassLoader().getResource("org/apache/commons/csv/CSVFileParser/bom.csv");
        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setHeader();
        try (final ICSVParser parser = CSVParser.parse(url, Charset.forName(UTF_8_NAME), format)) {
            for (final CSVRecord record : parser) {
                final String string = record.get("Date");
                assertNotNull(string);
                // System.out.println("date: " + record.get("Date"));
            }
        }
    }

    @Test
    public void testBOMInputStream_ParserWithInputStream() throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setHeader();
        try (final BOMInputStream inputStream = createBOMInputStream("org/apache/commons/csv/CSVFileParser/bom.csv");
             final ICSVParser parser = CSVParser.parse(inputStream, UTF_8, format)) {
            for (final CSVRecord record : parser) {
                final String string = record.get("Date");
                assertNotNull(string);
                // System.out.println("date: " + record.get("Date"));
            }
        }
    }

    @Test
    public void testBOMInputStream_ParserWithReader() throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setHeader();
        try (
                final Reader reader = new InputStreamReader(
                        createBOMInputStream("org/apache/commons/csv/CSVFileParser/bom.csv"), UTF_8_NAME);
                final ICSVParser parser = new CSVParser(reader, format)) {
            for (final CSVRecord record : parser) {
                final String string = record.get("Date");
                assertNotNull(string);
                // System.out.println("date: " + record.get("Date"));
            }
        }
    }

    @Test
    public void testBOMInputStream_parseWithReader() throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setHeader();
        try (
                final Reader reader = new InputStreamReader(
                        createBOMInputStream("org/apache/commons/csv/CSVFileParser/bom.csv"), UTF_8_NAME);
                final ICSVParser parser = CSVParser.parse(reader, format)) {
            for (final CSVRecord record : parser) {
                final String string = record.get("Date");
                assertNotNull(string);
                // System.out.println("date: " + record.get("Date"));
            }
        }
    }
}
