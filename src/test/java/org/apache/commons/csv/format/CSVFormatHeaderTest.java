package org.apache.commons.csv.format;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.apache.commons.csv.Constants.CR;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSVFormatHeaderTest {

    public enum Header {
        Name, Email, Phone
    }

    @Test
    public void testDuplicateHeaderElements() {
        final String[] header = { "A", "A" };
        final CSVFormat format = new CSVFormatBuilder().setHeaders(header).build();
        assertEquals(2, format.getHeader().length);
        assertArrayEquals(header, format.getHeader());
    }

    @Test
    public void testDuplicateHeaderElementsFalse() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CSVFormatBuilder().setAllowDuplicateHeaderNames(false).setHeaders("A", "A").build());
    }

    public void testDuplicateHeaderElementsTrue() {
        new CSVFormatBuilder().setAllowDuplicateHeaderNames(true).setHeaders("A", "A").build();
    }


    @Test
    public void testEqualsHeader() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setRecordSeparator(CR)
                .setCommentMarker('#')
                .setEscape('+')
                .setHeaders("One", "Two", "Three")
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        CSVFormat left = right.copy();
        left.setHeader(new String[]{"Three", "Two", "One"});

        assertNotEquals(right, left);
    }


    public void testEqualsSkipHeaderRecord() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setRecordSeparator(CR)
                .setCommentMarker('#')
                .setEscape('+')
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .setNullString("null")
                .setSkipHeaderRecord(true)
                .build();
        CSVFormat left = right.copy();
                left.setSkipHeaderRecord(false);

        assertNotEquals(right, left);
    }


    @Test
    public void testGetHeader() {
        final String[] header = {"one", "two", "three"};
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeaders(header).build();
        // getHeader() makes a copy of the header array.
        final String[] headerCopy = formatWithHeader.getHeader();
        headerCopy[0] = "A";
        headerCopy[1] = "B";
        headerCopy[2] = "C";
        assertFalse(Arrays.equals(formatWithHeader.getHeader(), headerCopy));
        assertNotSame(formatWithHeader.getHeader(), headerCopy);
    }

    @Test
    public void testWithHeaderEnum() {
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeader(Header.class).build();
        assertArrayEquals(new String[]{ "Name", "Email", "Phone" }, formatWithHeader.getHeader());
    }
}
