package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatBuilder;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.format.ICSVFormat;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringReader;
import java.util.List;

import static org.apache.commons.csv.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class CSVParserLineRecordTest {

    private static final String CSV_INPUT_1 = "a,b,c,d";

    private static final String CSV_INPUT_2 = "a,b,1 2";

    private static final String CSV_INPUT = "a,b,c,d\n" + " a , b , 1 2 \n" + "\"foo baar\", b,\n"
            // + " \"foo\n,,\n\"\",,\n\\\"\",d,e\n";
            + "   \"foo\n,,\n\"\",,\n\"\"\",d,e\n"; // changed to use standard CSV escaping


    private static final String[][] RESULT = {{"a", "b", "c", "d"}, {"a", "b", "1 2"}, {"foo baar", "b", ""},
            {"foo\n,,\n\",,\n\"", "d", "e"}};

    @Test
    public void testFirstEndOfLineCr() throws IOException {
        final String data = "foo\rbaar,\rhello,world\r,kanu";
        try (final CSVParser parser = CSVParser.parse(data, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
            assertEquals("\r", parser.getFirstEndOfLine());
        }
    }

    @Test
    public void testFirstEndOfLineCrLf() throws IOException {
        final String data = "foo\r\nbaar,\r\nhello,world\r\n,kanu";
        try (final CSVParser parser = CSVParser.parse(data, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
            assertEquals("\r\n", parser.getFirstEndOfLine());
        }
    }

    @Test
    public void testFirstEndOfLineLf() throws IOException {
        final String data = "foo\nbaar,\nhello,world\n,kanu";
        try (final CSVParser parser = CSVParser.parse(data, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
            assertEquals("\n", parser.getFirstEndOfLine());
        }
    }


    @Test
    public void testGetLine() throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setIgnoreSurroundingSpaces(true);
        try (final ICSVParser parser = CSVParser.parse(CSV_INPUT, format)) {
            for (final String[] re : RESULT) {
                assertArrayEquals(re, parser.nextRecord().values());
            }

            assertNull(parser.nextRecord());
        }
    }

    @Test
    public void testGetLineNumberWithCR() throws Exception {
        this.validateLineNumbers(String.valueOf(CR));
    }

    @Test
    public void testGetLineNumberWithCRLF() throws Exception {
        this.validateLineNumbers(CRLF);
    }

    @Test
    public void testGetLineNumberWithLF() throws Exception {
        this.validateLineNumbers(String.valueOf(LF));
    }

    @Test
    public void testGetOneLine() throws IOException {
        try (final ICSVParser parser = CSVParser.parse(CSV_INPUT_1, CSVFormatPredefinedFormats.Default.getFormat())) {
            final CSVRecord record = parser.getRecords().get(0);
            assertArrayEquals(RESULT[0], record.values());
        }
    }

    /**
     * Tests reusing a parser to process new string records one at a time as they are being discovered. See [CSV-110].
     *
     * @throws IOException when an I/O error occurs.
     */
    @Test
    public void testGetOneLineOneParser() throws IOException {
        final CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        try (final PipedWriter writer = new PipedWriter();
             final ICSVParser parser = new CSVParser(new PipedReader(writer), format)) {
            writer.append(CSV_INPUT_1);
            writer.append(format.getRecordSeparator());
            final CSVRecord record1 = parser.nextRecord();
            assertArrayEquals(RESULT[0], record1.values());
            writer.append(CSV_INPUT_2);
            writer.append(format.getRecordSeparator());
            final CSVRecord record2 = parser.nextRecord();
            assertArrayEquals(RESULT[1], record2.values());
        }
    }


    @Test
    public void testGetRecordNumberWithCR() throws Exception {
        this.validateRecordNumbers(String.valueOf(CR));
    }

    @Test
    public void testGetRecordNumberWithCRLF() throws Exception {
        this.validateRecordNumbers(CRLF);
    }

    @Test
    public void testGetRecordNumberWithLF() throws Exception {
        this.validateRecordNumbers(String.valueOf(LF));
    }

    @Test
    public void testGetRecordPositionWithCRLF() throws Exception {
        this.validateRecordPosition(CRLF);
    }

    @Test
    public void testGetRecordPositionWithLF() throws Exception {
        this.validateRecordPosition(String.valueOf(LF));
    }

    @Test
    public void testGetRecords() throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setIgnoreSurroundingSpaces(true);
        try (final ICSVParser parser = CSVParser.parse(CSV_INPUT, format)) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(RESULT.length, records.size());
            assertFalse(records.isEmpty());
            for (int i = 0; i < RESULT.length; i++) {
                assertArrayEquals(RESULT[i], records.get(i).values());
            }
        }
    }

    @Test
    public void testCarriageReturnEndings() throws IOException {
        final String code = "foo\rbaar,\rhello,world\r,kanu";
        try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
        }
    }

    @Test
    public void testCarriageReturnLineFeedEndings() throws IOException {
        final String code = "foo\r\nbaar,\r\nhello,world\r\n,kanu";
        try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
        }
    }

    @Test
    public void testLineFeedEndings() throws IOException {
        final String code = "foo\nbaar,\nhello,world\n,kanu";
        try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
        }
    }

    @Test
    public void testGetRecordWithMultiLineValues() throws Exception {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setRecordSeparator(CRLF);
        try (final ICSVParser parser = CSVParser.parse(
                "\"a\r\n1\",\"a\r\n2\"" + CRLF + "\"b\r\n1\",\"b\r\n2\"" + CRLF + "\"c\r\n1\",\"c\r\n2\"",
                format)) {
            CSVRecord record;
            assertEquals(0, parser.getRecordNumber());
            assertEquals(0, parser.getCurrentLineNumber());
            assertNotNull(record = parser.nextRecord());
            assertEquals(3, parser.getCurrentLineNumber());
            assertEquals(1, record.getRecordNumber());
            assertEquals(1, parser.getRecordNumber());
            assertNotNull(record = parser.nextRecord());
            assertEquals(6, parser.getCurrentLineNumber());
            assertEquals(2, record.getRecordNumber());
            assertEquals(2, parser.getRecordNumber());
            assertNotNull(record = parser.nextRecord());
            assertEquals(9, parser.getCurrentLineNumber());
            assertEquals(3, record.getRecordNumber());
            assertEquals(3, parser.getRecordNumber());
            assertNull(record = parser.nextRecord());
            assertEquals(9, parser.getCurrentLineNumber());
            assertEquals(3, parser.getRecordNumber());
        }
    }

    @Test
    public void testIgnoreEmptyLines() throws IOException {
        final String code = "\nfoo,baar\n\r\n,\n\n,world\r\n\n";
        // String code = "world\r\n\n";
        // String code = "foo;baar\r\n\r\nhello;\r\n\r\nworld;\r\n";
        try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(3, records.size());
        }
    }

    private void validateLineNumbers(final String lineSeparator) throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setRecordSeparator(lineSeparator);
        try (final ICSVParser parser = CSVParser.parse("a" + lineSeparator + "b" + lineSeparator + "c",
                format)) {
            assertEquals(0, parser.getCurrentLineNumber());
            assertNotNull(parser.nextRecord());
            assertEquals(1, parser.getCurrentLineNumber());
            assertNotNull(parser.nextRecord());
            assertEquals(2, parser.getCurrentLineNumber());
            assertNotNull(parser.nextRecord());
            // Read EOF without EOL should 3
            assertEquals(3, parser.getCurrentLineNumber());
            assertNull(parser.nextRecord());
            // Read EOF without EOL should 3
            assertEquals(3, parser.getCurrentLineNumber());
        }
    }

    private void validateRecordNumbers(final String lineSeparator) throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setRecordSeparator(lineSeparator);
        try (final ICSVParser parser = CSVParser.parse("a" + lineSeparator + "b" + lineSeparator + "c",
                format)) {
            CSVRecord record;
            assertEquals(0, parser.getRecordNumber());
            assertNotNull(record = parser.nextRecord());
            assertEquals(1, record.getRecordNumber());
            assertEquals(1, parser.getRecordNumber());
            assertNotNull(record = parser.nextRecord());
            assertEquals(2, record.getRecordNumber());
            assertEquals(2, parser.getRecordNumber());
            assertNotNull(record = parser.nextRecord());
            assertEquals(3, record.getRecordNumber());
            assertEquals(3, parser.getRecordNumber());
            assertNull(record = parser.nextRecord());
            assertEquals(3, parser.getRecordNumber());
        }
    }

    private void validateRecordPosition(final String lineSeparator) throws IOException {
        final String nl = lineSeparator; // used as linebreak in values for better distinction

        final String code = "a,b,c" + lineSeparator + "1,2,3" + lineSeparator +
                // to see if recordPosition correctly points to the enclosing quote
                "'A" + nl + "A','B" + nl + "B',CC" + lineSeparator +
                // unicode test... not very relevant while operating on strings instead of bytes, but for
                // completeness...
                "\u00c4,\u00d6,\u00dc" + lineSeparator + "EOF,EOF,EOF";

        final CSVFormat format = new CSVFormatBuilder().setDelimiter(',').setQuote('\'').setRecordSeparator(lineSeparator).build();
        ICSVParser parser = CSVParser.parse(code, format);

        CSVRecord record;
        assertEquals(0, parser.getRecordNumber());

        assertNotNull(record = parser.nextRecord());
        assertEquals(1, record.getRecordNumber());
        assertEquals(code.indexOf('a'), record.getCharacterPosition());

        assertNotNull(record = parser.nextRecord());
        assertEquals(2, record.getRecordNumber());
        assertEquals(code.indexOf('1'), record.getCharacterPosition());

        assertNotNull(record = parser.nextRecord());
        final long positionRecord3 = record.getCharacterPosition();
        assertEquals(3, record.getRecordNumber());
        assertEquals(code.indexOf("'A"), record.getCharacterPosition());
        assertEquals("A" + lineSeparator + "A", record.get(0));
        assertEquals("B" + lineSeparator + "B", record.get(1));
        assertEquals("CC", record.get(2));

        assertNotNull(record = parser.nextRecord());
        assertEquals(4, record.getRecordNumber());
        assertEquals(code.indexOf('\u00c4'), record.getCharacterPosition());

        assertNotNull(record = parser.nextRecord());
        assertEquals(5, record.getRecordNumber());
        assertEquals(code.indexOf("EOF"), record.getCharacterPosition());

        parser.close();

        // now try to read starting at record 3
        parser = new CSVParser(new StringReader(code.substring((int) positionRecord3)), format, positionRecord3, 3);

        assertNotNull(record = parser.nextRecord());
        assertEquals(3, record.getRecordNumber());
        assertEquals(code.indexOf("'A"), record.getCharacterPosition());
        assertEquals("A" + lineSeparator + "A", record.get(0));
        assertEquals("B" + lineSeparator + "B", record.get(1));
        assertEquals("CC", record.get(2));

        assertNotNull(record = parser.nextRecord());
        assertEquals(4, record.getRecordNumber());
        assertEquals(code.indexOf('\u00c4'), record.getCharacterPosition());
        assertEquals("\u00c4", record.get(0));

        parser.close();
    }
}
