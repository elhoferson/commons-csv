package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.QuoteMode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVPrinterDelimiterTest {

    @Test
    public void testDelimiterQuoted() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withQuote('\''))) {
            printer.print("a,b,c");
            printer.print("xyz");
            assertEquals("'a,b,c',xyz", sw.toString());
        }
    }

    @Test
    public void testDelimiterQuoteNone() throws IOException {
        final StringWriter sw = new StringWriter();
        final CSVFormat format = CSVFormat.DEFAULT.withEscape('!').withQuoteMode(QuoteMode.NONE);
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("a,b,c");
            printer.print("xyz");
            assertEquals("a!,b!,c,xyz", sw.toString());
        }
    }

    @Test
    public void testDelimiterStringQuoted() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder().setDelimiter("[|]").setQuote('\'').build())) {
            printer.print("a[|]b[|]c");
            printer.print("xyz");
            assertEquals("'a[|]b[|]c'[|]xyz", sw.toString());
        }
    }

    @Test
    public void testDelimiterStringQuoteNone() throws IOException {
        final StringWriter sw = new StringWriter();
        final CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter("[|]").setEscape('!').setQuoteMode(QuoteMode.NONE).build();
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print("a[|]b[|]c");
            printer.print("xyz");
            printer.print("a[xy]bc[]");
            assertEquals("a![!|!]b![!|!]c[|]xyz[|]a[xy]bc[]", sw.toString());
        }
    }

    @Test
    public void testDelimiterEscaped() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withEscape('!').withQuote(null))) {
            printer.print("a,b,c");
            printer.print("xyz");
            assertEquals("a!,b!,c,xyz", sw.toString());
        }
    }

    @Test
    public void testDelimiterPlain() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withQuote(null))) {
            printer.print("a,b,c");
            printer.print("xyz");
            assertEquals("a,b,c,xyz", sw.toString());
        }
    }

    @Test
    public void testDelimiterStringEscaped() throws IOException {
        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder().setDelimiter("|||").setEscape('!').setQuote(null).build())) {
            printer.print("a|||b|||c");
            printer.print("xyz");
            assertEquals("a!|!|!|b!|!|!|c|||xyz", sw.toString());
        }
    }
}
