package org.apache.commons.csv.format;

import org.apache.commons.csv.printer.CSVPrinter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.apache.commons.csv.Constants.LF;
import static org.junit.jupiter.api.Assertions.*;

public class CSVFormatQuoteTest {

    @Test
    public void testEqualsLeftNoQuoteRightQuote() {
        CSVFormat left = CSVFormat.newFormat(',');
        left.setQuoteCharacter(null);
        CSVFormat right = left.copy();
                right.setQuoteCharacter('#');

        assertNotEquals(left, right);
    }


    @Test
    public void testEqualsNoQuotes() {
        CSVFormat left = CSVFormat.newFormat(',');
        left.setQuoteCharacter(null);
        CSVFormat right = left.copy();
        right.setQuoteCharacter(null);

        assertEquals(left, right);
    }

    @Test
    public void testEqualsQuoteChar() {
        CSVFormat left = CSVFormat.newFormat('\'');
        left.setQuoteCharacter('"');
        CSVFormat right = left.copy();
        right.setQuoteCharacter('!');

        assertNotEquals(right, left);
    }

    @Test
    public void testEqualsQuotePolicy() {
        CSVFormat left = CSVFormat.newFormat('\'');
        left.setQuoteCharacter('"');
        left.setQuoteMode(QuoteMode.ALL);
        CSVFormat right = left.copy();
        right.setQuoteMode(QuoteMode.MINIMAL);

        assertNotEquals(right, left);
    }

    @Test
    public void testPrintWithoutQuotes() throws IOException {
        final Reader in = new StringReader("");
        final Appendable out = new StringBuilder();
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setDelimiter(",");
        format.setQuoteCharacter('"');
        format.setEscapeCharacter('?');
        format.setQuoteMode(QuoteMode.NON_NUMERIC);
        CSVPrinter csvPrinter = new CSVPrinter(out, format);
        csvPrinter.print(in, true);
        assertEquals("\"\"", out.toString());
    }

    @Test
    public void testPrintWithQuoteModeIsNONE() throws IOException {
        final Reader in = new StringReader("a,b,c");
        final Appendable out = new StringBuilder();
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setDelimiter(",");
        format.setQuoteCharacter('"');
        format.setEscapeCharacter('?');
        format.setQuoteMode(QuoteMode.NONE);
        CSVPrinter csvPrinter = new CSVPrinter(out, format);
        csvPrinter.print(in, true);
        assertEquals("a?,b?,c", out.toString());
    }

    @Test
    public void testPrintWithQuotes() throws IOException {
        final Reader in = new StringReader("\"a,b,c\r\nx,y,z");
        final Appendable out = new StringBuilder();
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setDelimiter(",");
        format.setQuoteCharacter('"');
        format.setEscapeCharacter('?');
        format.setQuoteMode(QuoteMode.NON_NUMERIC);
        CSVPrinter csvPrinter = new CSVPrinter(out, format);
        csvPrinter.print(in, true);
        assertEquals("\"\"\"a,b,c\r\nx,y,z\"", out.toString());
    }

    @Test
    public void testQuoteCharSameAsCommentStartThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setQuote('!').setCommentMarker('!').build());
    }
    @Test
    public void testQuoteCharSameAsCommentStartThrowsExceptionForWrapperType() {
        // Cannot assume that callers won't use different Character objects
        assertThrows(
                IllegalArgumentException.class,
                () -> new CSVFormatBuilder().setQuote(Character.valueOf('!')).setCommentMarker('!').build());
    }

    @Test
    public void testQuoteCharSameAsDelimiterThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setQuote('!').setDelimiter('!').build());
    }

    @Test
    public void testQuotePolicyNoneWithoutEscapeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setDelimiter('!').setQuoteMode(QuoteMode.NONE).build());
    }

    @Test
    public void testWithQuoteChar() {
        final CSVFormat formatWithQuoteChar = new CSVFormatBuilder().setQuote('"').build();
        assertEquals(Character.valueOf('"'), formatWithQuoteChar.getQuoteCharacter());
    }

    @Test
    public void testWithQuoteLFThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setQuote(LF).build());
    }

    @Test
    public void testWithQuotePolicy() {
        final CSVFormat formatWithQuotePolicy = new CSVFormatBuilder().setQuoteMode(QuoteMode.ALL).build();
        assertEquals(QuoteMode.ALL, formatWithQuotePolicy.getQuoteMode());
    }
}
