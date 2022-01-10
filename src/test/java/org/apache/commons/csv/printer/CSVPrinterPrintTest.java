package org.apache.commons.csv.printer;

import org.apache.commons.csv.enums.EmptyEnum;
import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.format.QuoteMode;
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.record.CSVRecord;
import org.apache.commons.csv.util.IOUtils;
import org.apache.commons.csv.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CSVPrinterPrintTest extends AbstractCSVPrinterTest {

    @Test
    public void testPrint() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\\c");
            assertEquals("a,b\\c" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrintCSVParser() throws IOException {
        final String code = "a1,b1\n" // 1)
                + "a2,b2\n" // 2)
                + "a3,b3\n" // 3)
                + "a4,b4\n"// 4)
                ;
        final String[][] res = {{"a1", "b1"}, {"a2", "b2"}, {"a3", "b3"}, {"a4", "b4"}};
        final CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, format); final ICSVParser parser = CSVParser.parse(code, format)) {
            printer.printRecords(parser);
        }
        try (final ICSVParser parser = CSVParser.parse(sw.toString(), format)) {
            final List<CSVRecord> records = parser.getRecords();
            assertFalse(records.isEmpty());
            Utils.compare("Fail", res, records);
        }
    }

    @Test
    public void testPrintCSVRecord() throws IOException {
        final String code = "a1,b1\n" // 1)
                + "a2,b2\n" // 2)
                + "a3,b3\n" // 3)
                + "a4,b4\n"// 4)
                ;
        final String[][] res = {{"a1", "b1"}, {"a2", "b2"}, {"a3", "b3"}, {"a4", "b4"}};
        final CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, format); final ICSVParser parser = CSVParser.parse(code, format)) {
            for (final CSVRecord record : parser) {
                printer.printRecord(record);
            }
        }
        try (final ICSVParser parser = CSVParser.parse(sw.toString(), format)) {
            final List<CSVRecord> records = parser.getRecords();
            assertFalse(records.isEmpty());
            Utils.compare("Fail", res, records);
        }
    }

    @Test
    public void testPrintCSVRecords() throws IOException {
        final String code = "a1,b1\n" // 1)
                + "a2,b2\n" // 2)
                + "a3,b3\n" // 3)
                + "a4,b4\n"// 4)
                ;
        final String[][] res = {{"a1", "b1"}, {"a2", "b2"}, {"a3", "b3"}, {"a4", "b4"}};
        final CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, format); final ICSVParser parser = CSVParser.parse(code, format)) {
            printer.printRecords(parser.getRecords());
        }
        try (final ICSVParser parser = CSVParser.parse(sw.toString(), format)) {
            final List<CSVRecord> records = parser.getRecords();
            assertFalse(records.isEmpty());
            Utils.compare("Fail", res, records);
        }
    }

    @Test
    public void testPrintCustomNullValues() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setNullString("NULL");
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.printRecord("a", null, "b");
            assertEquals("a,NULL,b" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter1() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b");
            assertEquals("a,b" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter2() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a,b", "b");
            assertEquals("\"a,b\",b" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter3() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a, b", "b ");
            assertEquals("\"a, b\",\"b \"" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter4() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\"c");
            assertEquals("a,\"b\"\"c\"" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter5() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\nc");
            assertEquals("a,\"b\nc\"" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter6() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\r\nc");
            assertEquals("a,\"b\r\nc\"" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrinter7() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\\c");
            assertEquals("a,b\\c" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrintNullValues() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", null, "b");
            assertEquals("a,,b" + recordSeparator, sw.toString());
        }
    }

    @Test
    public void testPrintOnePositiveInteger() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteMode(QuoteMode.MINIMAL);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print(Integer.MAX_VALUE);
            assertEquals(String.valueOf(Integer.MAX_VALUE), sw.toString());
        }
    }

    /**
     * Test to target the use of {@link IOUtils#copy(java.io.Reader, Appendable)} which directly
     * buffers the value from the Reader to the Appendable.
     *
     * <p>Requires the format to have no quote or escape character, value to be a
     * {@link java.io.Reader Reader} and the output <i>MUST NOT</i> be a
     * {@link java.io.Writer Writer} but some other Appendable.</p>
     *
     * @throws IOException Not expected to happen
     */
    @Test
    public void testPrintReaderWithoutQuoteToAppendable() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final String content = "testValue";
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sb, format)) {
            final StringReader value = new StringReader(content);
            printer.print(value);
        }
        assertEquals(content, sb.toString());
    }

    /**
     * Test to target the use of {@link IOUtils#copyLarge(java.io.Reader, Writer)} which directly
     * buffers the value from the Reader to the Writer.
     *
     * <p>Requires the format to have no quote or escape character, value to be a
     * {@link java.io.Reader Reader} and the output <i>MUST</i> be a
     * {@link java.io.Writer Writer}.</p>
     *
     * @throws IOException Not expected to happen
     */
    @Test
    public void testPrintReaderWithoutQuoteToWriter() throws IOException {
        final StringWriter sw = new StringWriter();
        final String content = "testValue";
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            final StringReader value = new StringReader(content);
            printer.print(value);
        }
        assertEquals(content, sw.toString());
    }

    @Test
    public void testPrintRecordsWithCSVRecord() throws IOException {
        final String[] values = {"A", "B", "C"};
        final String rowData = StringUtils.join(values, ',');
        final CharArrayWriter charArrayWriter = new CharArrayWriter(0);
        try (final ICSVParser parser = new CSVParser(new StringReader(rowData), CSVFormatPredefinedFormats.Default.getFormat());
             final CSVPrinter csvPrinter = new CSVPrinter(charArrayWriter, CSVFormatPredefinedFormats.InformixUnload.getFormat())) {
            for (final CSVRecord record : parser) {
                csvPrinter.printRecord(record);
            }
        }
        assertEquals(6, charArrayWriter.size());
        assertEquals("A|B|C" + CSVFormatPredefinedFormats.InformixUnload.getFormat().getRecordSeparator(), charArrayWriter.toString());
    }

    @Test
    public void testPrintRecordsWithEmptyVector() throws IOException {
        final PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM));
            try (CSVPrinter csvPrinter = new CSVPrinter(System.out, CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat())) {
                final Vector<EmptyEnum> vector = new Vector<>();
                final int expectedCapacity = 23;
                vector.setSize(expectedCapacity);
                csvPrinter.printRecords(vector);
                assertEquals(expectedCapacity, vector.capacity());
            }
        } finally {
            System.setOut(out);
        }
    }

    @Test
    public void testPrintRecordsWithObjectArray() throws IOException {
        final CharArrayWriter charArrayWriter = new CharArrayWriter(0);
        try (CSVPrinter csvPrinter = new CSVPrinter(charArrayWriter, CSVFormatPredefinedFormats.InformixUnload.getFormat())) {
            final HashSet<BatchUpdateException> hashSet = new HashSet<>();
            final Object[] objectArray = new Object[6];
            objectArray[3] = hashSet;
            csvPrinter.printRecords(objectArray);
        }
        assertEquals(6, charArrayWriter.size());
        assertEquals("\n\n\n\n\n\n", charArrayWriter.toString());
    }

    @Test
    public void testPrintRecordsWithResultSetOneRow() throws IOException, SQLException {
        try (CSVPrinter csvPrinter = new CSVPrinter(System.out, CSVFormatPredefinedFormats.MySQL.getFormat())) {
            try (ResultSet resultSet = new SimpleResultSet()) {
                csvPrinter.printRecords(resultSet);
                assertEquals(0, resultSet.getRow());
            }
        }
    }

    @Test
    public void testPrintToFileWithCharsetUtf16Be() throws IOException {
        final File file = File.createTempFile(getClass().getName(), ".csv");
        try (final CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(file),
                StandardCharsets.UTF_16BE),
                CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\\c");
        }
        assertEquals("a,b\\c" + recordSeparator, FileUtils.readFileToString(file, StandardCharsets.UTF_16BE));
    }

    @Test
    public void testPrintToFileWithDefaultCharset() throws IOException {
        final File file = File.createTempFile(getClass().getName(), ".csv");
        try (final CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(file),
                Charset.defaultCharset()),
                CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\\c");
        }
        assertEquals("a,b\\c" + recordSeparator, FileUtils.readFileToString(file, Charset.defaultCharset()));
    }

    @Test
    public void testPrintToPathWithDefaultCharset() throws IOException {
        final File file = File.createTempFile(getClass().getName(), ".csv");
        try (final CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(file.toPath(),
                Charset.defaultCharset()),
                CSVFormatPredefinedFormats.Default.getFormat())) {
            printer.printRecord("a", "b\\c");
        }
        assertEquals("a,b\\c" + recordSeparator, FileUtils.readFileToString(file, Charset.defaultCharset()));
    }

}
