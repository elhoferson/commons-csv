package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CSVParserEmptyTest {

    @Test
    public void testEmptyFile() throws Exception {
        try (final ICSVParser parser = CSVParser.parse(Paths.get("src/test/resources/org/apache/commons/csv/empty.txt"),
                StandardCharsets.UTF_8, CSVFormatPredefinedFormats.Default.getFormat())) {
            assertNull(parser.nextRecord());
        }
    }

    @Test
    public void testEmptyFileHeaderParsing() throws Exception {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader();
        format.setSkipHeaderRecord(true);
        try (final ICSVParser parser = CSVParser.parse("", format)) {
            assertNull(parser.nextRecord());
            assertTrue(parser.getHeaderNames().isEmpty());
        }
    }

    @Test
    public void testEmptyLineBehaviorCSV() throws Exception {
        final String[] codes = {"hello,\r\n\r\n\r\n", "hello,\n\n\n", "hello,\"\"\r\n\r\n\r\n", "hello,\"\"\n\n\n"};
        final String[][] res = {{"hello", ""} // CSV format ignores empty lines
        };
        for (final String code : codes) {
            try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Default.getFormat())) {
                final List<CSVRecord> records = parser.getRecords();
                assertEquals(res.length, records.size());
                assertFalse(records.isEmpty());
                for (int i = 0; i < res.length; i++) {
                    assertArrayEquals(res[i], records.get(i).values());
                }
            }
        }
    }

    @Test
    public void testEmptyLineBehaviorExcel() throws Exception {
        final String[] codes = {"hello,\r\n\r\n\r\n", "hello,\n\n\n", "hello,\"\"\r\n\r\n\r\n", "hello,\"\"\n\n\n"};
        final String[][] res = {{"hello", ""}, {""}, // Excel format does not ignore empty lines
                {""}};
        for (final String code : codes) {
            try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Excel.getFormat())) {
                final List<CSVRecord> records = parser.getRecords();
                assertEquals(res.length, records.size());
                assertFalse(records.isEmpty());
                for (int i = 0; i < res.length; i++) {
                    assertArrayEquals(res[i], records.get(i).values());
                }
            }
        }
    }

    @Test
    public void testEmptyString() throws Exception {
        try (final ICSVParser parser = CSVParser.parse("", CSVFormatPredefinedFormats.Default.getFormat())) {
            assertNull(parser.nextRecord());
        }
    }
}
