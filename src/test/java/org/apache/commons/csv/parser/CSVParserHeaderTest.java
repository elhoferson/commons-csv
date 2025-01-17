package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatBuilder;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CSVParserHeaderTest {

    @Test
    public void testHeader() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");

        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();

        for (int i = 0; i < 2; i++) {
            assertTrue(records.hasNext());
            final CSVRecord record = records.next();
            assertEquals(record.get(0), record.get("a"));
            assertEquals(record.get(1), record.get("b"));
            assertEquals(record.get(2), record.get("c"));
        }

        assertFalse(records.hasNext());
    }

    @Test
    public void testHeaderComment() throws Exception {
        final Reader in = new StringReader("# comment\na,b,c\n1,2,3\nx,y,z");

        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setCommentMarker('#').setHeaders().build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();

        for (int i = 0; i < 2; i++) {
            assertTrue(records.hasNext());
            final CSVRecord record = records.next();
            assertEquals(record.get(0), record.get("a"));
            assertEquals(record.get(1), record.get("b"));
            assertEquals(record.get(2), record.get("c"));
        }

        assertFalse(records.hasNext());
    }

    @Test
    public void testHeaderMissing() throws Exception {
        final Reader in = new StringReader("a,,c\n1,2,3\nx,y,z");

        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().setAllowMissingColumnNames(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();

        for (int i = 0; i < 2; i++) {
            assertTrue(records.hasNext());
            final CSVRecord record = records.next();
            assertEquals(record.get(0), record.get("a"));
            assertEquals(record.get(2), record.get("c"));
        }

        assertFalse(records.hasNext());
    }

    @Test
    public void testHeaderMissingWithNull() throws Exception {
        final Reader in = new StringReader("a,,c,,e\n1,2,3,4,5\nv,w,x,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().setAllowMissingColumnNames(true).setNullString("").build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
    }

    @Test
    public void testHeadersMissing() throws Exception {
        final Reader in = new StringReader("a,,c,,e\n1,2,3,4,5\nv,w,x,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().setAllowMissingColumnNames(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
    }

    @Test
    public void testHeadersMissingException() {
        final Reader in = new StringReader("a,,c,,e\n1,2,3,4,5\nv,w,x,y,z");
        assertThrows(IllegalArgumentException.class, () -> new CSVParser(in, new CSVFormatBuilder().setHeaders().build()).iterator());
    }

    @Test
    public void testHeadersMissingOneColumnException() throws Exception {
        final Reader in = new StringReader("a,,c,d,e\n1,2,3,4,5\nv,w,x,y,z");
        assertThrows(IllegalArgumentException.class, () -> new CSVParser(in, new CSVFormatBuilder().setHeaders().build()).iterator());
    }

    @Test
    public void testHeadersWithNullColumnName() throws IOException {
        final Reader in = new StringReader("header1,null,header3\n1,2,3\n4,5,6");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().setAllowMissingColumnNames(true).setNullString("null").build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        // Expect the null header to be missing
        assertEquals(Arrays.asList("header1", "header3"), record.getParser().getHeaderNames());
        assertEquals(2, record.getParser().getHeaderMap().size());
    }

    @Test
    public void testIgnoreCaseHeaderMapping() throws Exception {
        final Reader reader = new StringReader("1,2,3");
        final ICSVParser ICSVParser = new CSVParser(reader, new CSVFormatBuilder().setHeaders("One", "TWO", "three").setIgnoreHeaderCase(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("one"));
        assertEquals("2", record.get("two"));
        assertEquals("3", record.get("THREE"));
    }


    @Test
    public void testDuplicateHeadersAllowedByDefault() throws Exception {
        CSVParser.parse("a,b,a\n1,2,3\nx,y,z", new CSVFormatBuilder().setHeaders().build());
    }

    @Test
    public void testDuplicateHeadersNotAllowed() {
        assertThrows(IllegalArgumentException.class, () -> CSVParser.parse("a,b,a\n1,2,3\nx,y,z",
                new CSVFormatBuilder().setHeaders().setAllowDuplicateHeaderNames(false).build()));
    }


    @Test
    public void testGetHeaderMap() throws Exception {
        try (final ICSVParser parser = CSVParser.parse("a,b,c\n1,2,3\nx,y,z",
                new CSVFormatBuilder().setHeaders("A", "B", "C").build())) {
            final Map<String, Integer> headerMap = parser.getHeaderMap();
            final Iterator<String> columnNames = headerMap.keySet().iterator();
            // Headers are iterated in column order.
            assertEquals("A", columnNames.next());
            assertEquals("B", columnNames.next());
            assertEquals("C", columnNames.next());
            final Iterator<CSVRecord> records = parser.iterator();

            // Parse to make sure getHeaderMap did not have a side-effect.
            for (int i = 0; i < 3; i++) {
                assertTrue(records.hasNext());
                final CSVRecord record = records.next();
                assertEquals(record.get(0), record.get("A"));
                assertEquals(record.get(1), record.get("B"));
                assertEquals(record.get(2), record.get("C"));
            }

            assertFalse(records.hasNext());
        }
    }

    @Test
    public void testGetHeaderNames() throws IOException {
        try (final ICSVParser parser = CSVParser.parse("a,b,c\n1,2,3\nx,y,z",
                new CSVFormatBuilder().setHeaders("A", "B", "C").build())) {
            final Map<String, Integer> nameIndexMap = parser.getHeaderMap();
            final List<String> headerNames = parser.getHeaderNames();
            assertNotNull(headerNames);
            assertEquals(nameIndexMap.size(), headerNames.size());
            for (int i = 0; i < headerNames.size(); i++) {
                final String name = headerNames.get(i);
                assertEquals(i, nameIndexMap.get(name).intValue());
            }
        }
    }

    @Test
    public void testGetHeaderNamesReadOnly() throws IOException {
        try (final ICSVParser parser = CSVParser.parse("a,b,c\n1,2,3\nx,y,z",
                new CSVFormatBuilder().setHeaders("A", "B", "C").build())) {
            final List<String> headerNames = parser.getHeaderNames();
            assertNotNull(headerNames);
            assertThrows(UnsupportedOperationException.class, () -> headerNames.add("This is a read-only list."));
        }
    }


    @Test
    public void testProvidedHeader() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");

        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders("A", "B", "C").build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();

        for (int i = 0; i < 3; i++) {
            assertTrue(records.hasNext());
            final CSVRecord record = records.next();
            assertTrue(record.isMapped("A"));
            assertTrue(record.isMapped("B"));
            assertTrue(record.isMapped("C"));
            assertFalse(record.isMapped("NOT MAPPED"));
            assertEquals(record.get(0), record.get("A"));
            assertEquals(record.get(1), record.get("B"));
            assertEquals(record.get(2), record.get("C"));
        }

        assertFalse(records.hasNext());
    }

    @Test
    public void testProvidedHeaderAuto() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");

        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();

        for (int i = 0; i < 2; i++) {
            assertTrue(records.hasNext());
            final CSVRecord record = records.next();
            assertTrue(record.isMapped("a"));
            assertTrue(record.isMapped("b"));
            assertTrue(record.isMapped("c"));
            assertFalse(record.isMapped("NOT MAPPED"));
            assertEquals(record.get(0), record.get("a"));
            assertEquals(record.get(1), record.get("b"));
            assertEquals(record.get(2), record.get("c"));
        }

        assertFalse(records.hasNext());
    }

    @Test
    public void testRepeatedHeadersAreReturnedInCSVRecordHeaderNames() throws IOException {
        final Reader in = new StringReader("header1,header2,header1\n1,2,3\n4,5,6");

        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().setSkipHeaderRecord(true).setTrim(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals(Arrays.asList("header1", "header2", "header1"), record.getParser().getHeaderNames());
    }


    @Test
    public void testSkipAutoHeader() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders().build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("a"));
        assertEquals("2", record.get("b"));
        assertEquals("3", record.get("c"));
    }

    @Test
    public void testSkipHeaderOverrideDuplicateHeaders() throws Exception {
        final Reader in = new StringReader("a,a,a\n1,2,3\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders("X", "Y", "Z").setSkipHeaderRecord(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("X"));
        assertEquals("2", record.get("Y"));
        assertEquals("3", record.get("Z"));
    }

    @Test
    public void testSkipSetAltHeaders() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders("X", "Y", "Z").setSkipHeaderRecord(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("X"));
        assertEquals("2", record.get("Y"));
        assertEquals("3", record.get("Z"));
    }

    @Test
    public void testSkipSetHeader() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders("a", "b", "c").setSkipHeaderRecord(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("a"));
        assertEquals("2", record.get("b"));
        assertEquals("3", record.get("c"));
    }

    @Test
    @Disabled
    public void testStartWithEmptyLinesThenHeaders() throws Exception {
        final String[] codes = {"\r\n\r\n\r\nhello,\r\n\r\n\r\n", "hello,\n\n\n", "hello,\"\"\r\n\r\n\r\n",
                "hello,\"\"\n\n\n"};
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

}
