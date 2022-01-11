package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVPrinterEscapeTest {

    private static final char QUOTE_CH = '\'';

    @Test
    public void testEscapeBackslash1() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(QUOTE_CH);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\");
        }
        assertEquals("\\", sw.toString());
    }

    @Test
    public void testEscapeBackslash2() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(QUOTE_CH);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\\r");
        }
        assertEquals("'\\\r'", sw.toString());
    }

    @Test
    public void testEscapeBackslash3() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(QUOTE_CH);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("X\\\r");
        }
        assertEquals("'X\\\r'", sw.toString());
    }

    @Test
    public void testEscapeBackslash4() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(QUOTE_CH);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\\\");
        }
        assertEquals("\\\\", sw.toString());
    }

    @Test
    public void testEscapeBackslash5() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(QUOTE_CH);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\\\");
        }
        assertEquals("\\\\", sw.toString());
    }

    @Test
    public void testEscapeNull1() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setEscapeCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\");
        }
        assertEquals("\\", sw.toString());
    }

    @Test
    public void testEscapeNull2() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setEscapeCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\\r");
        }
        assertEquals("\"\\\r\"", sw.toString());
    }

    @Test
    public void testEscapeNull3() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setEscapeCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("X\\\r");
        }
        assertEquals("\"X\\\r\"", sw.toString());
    }

    @Test
    public void testEscapeNull4() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setEscapeCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\\\");
        }
        assertEquals("\\\\", sw.toString());
    }

    @Test
    public void testEscapeNull5() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setEscapeCharacter(null);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("\\\\");
        }
        assertEquals("\\\\", sw.toString());
    }
}
