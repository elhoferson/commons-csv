package org.apache.commons.csv.format;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.LF;
import static org.junit.jupiter.api.Assertions.*;

public class CSVFormatEqualsTest {

    private static void assertNotEquals(final Object right, final Object left) {
        Assertions.assertNotEquals(right, left);
        Assertions.assertNotEquals(left, right);
    }

    private void assertNotEquals(final String name, final String type, final Object left, final Object right) {
        if (left.equals(right) || right.equals(left)) {
            fail("Objects must not compare equal for " + name + "(" + type + ")");
        }
        if (left.hashCode() == right.hashCode()) {
            fail("Hash code should not be equal for " + name + "(" + type + ")");
        }
    }

    @Test
    public void testEquals() {
        final CSVFormat right = CSVFormatPredefinedFormats.Default.getFormat();
        final CSVFormat left = right.copy();

        Assertions.assertNotEquals(null, right);
        Assertions.assertNotEquals("A String Instance", right);

        assertEquals(right, right);
        assertEquals(right, left);
        assertEquals(left, right);

        assertEquals(right.hashCode(), right.hashCode());
        assertEquals(right.hashCode(), left.hashCode());
    }

    @Test
    public void testEqualsCommentStart() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setQuote('"')
                .setCommentMarker('#')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        final CSVFormat left = right.copy();
                left.setCommentMarker('!');

        assertNotEquals(right, left);
    }


    @Test
    public void testEqualsDelimiter() {
        final CSVFormat right = CSVFormat.newFormat('!');
        final CSVFormat left = CSVFormat.newFormat('?');

        assertNotEquals(right, left);
    }

    @Test
    public void testEqualsEscape() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setQuote('"')
                .setCommentMarker('#')
                .setEscape('+')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        final CSVFormat left = right.copy();
                left.setEscapeCharacter('!');

        assertNotEquals(right, left);
    }



    @Test
    public void testEqualsHash() throws Exception {
        final Method[] methods = CSVFormat.class.getDeclaredMethods();
        for (final Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                final String name = method.getName();
                if (name.startsWith("with")) {
                    for (final Class<?> cls : method.getParameterTypes()) {
                        final String type = cls.getCanonicalName();
                        if ("boolean".equals(type)) {
                            final Object defTrue = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), Boolean.TRUE);
                            final Object defFalse = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), Boolean.FALSE);
                            assertNotEquals(name, type ,defTrue, defFalse);
                        } else if ("char".equals(type)){
                            final Object a = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), 'a');
                            final Object b = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), 'b');
                            assertNotEquals(name, type, a, b);
                        } else if ("java.lang.Character".equals(type)){
                            final Object a = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), new Object[] {null});
                            final Object b = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), Character.valueOf('d'));
                            assertNotEquals(name, type, a, b);
                        } else if ("java.lang.String".equals(type)){
                            final Object a = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), new Object[] {null});
                            final Object b = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), "e");
                            assertNotEquals(name, type, a, b);
                        } else if ("java.lang.String[]".equals(type)){
                            final Object a = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), new Object[] {new String[] {null, null}});
                            final Object b = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), new Object[] {new String[] {"f", "g"}});
                            assertNotEquals(name, type, a, b);
                        } else if ("org.apache.commons.csv.format.QuoteMode".equals(type)){
                            final Object a = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), QuoteMode.MINIMAL);
                            final Object b = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), QuoteMode.ALL);
                            assertNotEquals(name, type, a, b);
                        } else if ("java.lang.Object[]".equals(type)){
                            final Object a = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), new Object[] {new Object[] {null, null}});
                            final Object b = method.invoke(CSVFormatPredefinedFormats.Default.getFormat(), new Object[] {new Object[] {new Object(), new Object()}});
                            assertNotEquals(name, type, a, b);
                        } else if ("withHeader".equals(name)){ // covered above by String[]
                            // ignored
                        } else {
                            fail("Unhandled method: "+name + "(" + type + ")");
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testEqualsIgnoreEmptyLines() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setCommentMarker('#')
                .setEscape('+')
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        final CSVFormat left = right.copy();
                left.setIgnoreEmptyLines(false);


        assertNotEquals(right, left);
    }



    @Test
    public void testEqualsIgnoreSurroundingSpaces() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setCommentMarker('#')
                .setEscape('+')
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        final CSVFormat left = right.copy();
                left.setIgnoreSurroundingSpaces(false);

        assertNotEquals(right, left);
    }




    @Test
    public void testEqualsNullString() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setRecordSeparator(CR)
                .setCommentMarker('#')
                .setEscape('+')
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .setNullString("null")
                .build();
        final CSVFormat left = right.copy();
                left.setNullString("---");


        assertNotEquals(right, left);
    }



    @Test
    public void testEqualsOne() {

        final CSVFormat csvFormatOne = CSVFormatPredefinedFormats.InformixUnload.getFormat();
        final CSVFormat csvFormatTwo = CSVFormatPredefinedFormats.MySQL.getFormat();


        assertEquals('\\', (char)csvFormatOne.getEscapeCharacter());
        assertNull(csvFormatOne.getQuoteMode());

        assertTrue(csvFormatOne.getIgnoreEmptyLines());
        assertFalse(csvFormatOne.getSkipHeaderRecord());

        assertFalse(csvFormatOne.getIgnoreHeaderCase());
        assertNull(csvFormatOne.getCommentMarker());

        assertFalse(csvFormatOne.isCommentMarkerSet());
        assertTrue(csvFormatOne.isQuoteCharacterSet());

        assertEquals('|', csvFormatOne.getDelimiter());
        assertFalse(csvFormatOne.getAllowMissingColumnNames());

        assertTrue(csvFormatOne.isEscapeCharacterSet());
        assertEquals("\n", csvFormatOne.getRecordSeparator());

        assertEquals('\"', (char)csvFormatOne.getQuoteCharacter());
        assertFalse(csvFormatOne.getTrailingDelimiter());

        assertFalse(csvFormatOne.getTrim());
        assertFalse(csvFormatOne.isNullStringSet());

        assertNull(csvFormatOne.getNullString());
        assertFalse(csvFormatOne.getIgnoreSurroundingSpaces());


        assertTrue(csvFormatTwo.isEscapeCharacterSet());
        assertNull(csvFormatTwo.getQuoteCharacter());

        assertFalse(csvFormatTwo.getAllowMissingColumnNames());
        assertEquals(QuoteMode.ALL_NON_NULL, csvFormatTwo.getQuoteMode());

        assertEquals('\t', csvFormatTwo.getDelimiter());
        assertEquals("\n", csvFormatTwo.getRecordSeparator());

        assertFalse(csvFormatTwo.isQuoteCharacterSet());
        assertTrue(csvFormatTwo.isNullStringSet());

        assertEquals('\\', (char)csvFormatTwo.getEscapeCharacter());
        assertFalse(csvFormatTwo.getIgnoreHeaderCase());

        assertFalse(csvFormatTwo.getTrim());
        assertFalse(csvFormatTwo.getIgnoreEmptyLines());

        assertEquals("\\N", csvFormatTwo.getNullString());
        assertFalse(csvFormatTwo.getIgnoreSurroundingSpaces());

        assertFalse(csvFormatTwo.getTrailingDelimiter());
        assertFalse(csvFormatTwo.getSkipHeaderRecord());

        assertNull(csvFormatTwo.getCommentMarker());
        assertFalse(csvFormatTwo.isCommentMarkerSet());

        assertNotSame(csvFormatOne, csvFormatTwo);
        assertNotSame(csvFormatTwo, csvFormatOne);

        Assertions.assertNotEquals(csvFormatOne, csvFormatTwo);
        Assertions.assertNotEquals(csvFormatTwo, csvFormatOne);
    }


    @Test
    public void testEqualsRecordSeparator() {
        final CSVFormat right = new CSVFormatBuilder()
                .setRecordSeparator(CR)
                .setCommentMarker('#')
                .setEscape('+')
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        final CSVFormat left = right.copy();
                left.setRecordSeparator(LF);

        assertNotEquals(right, left);
    }




    @Test
    public void testEqualsWithNull() {

        final CSVFormat csvFormat = CSVFormatPredefinedFormats.PostgreSQLText.getFormat();

        assertEquals('\\', (char)csvFormat.getEscapeCharacter());
        assertFalse(csvFormat.getIgnoreSurroundingSpaces());

        assertFalse(csvFormat.getTrailingDelimiter());
        assertFalse(csvFormat.getTrim());

        assertTrue(csvFormat.isQuoteCharacterSet());
        assertEquals("\\N", csvFormat.getNullString());

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertTrue(csvFormat.isEscapeCharacterSet());

        assertFalse(csvFormat.isCommentMarkerSet());
        assertNull(csvFormat.getCommentMarker());

        assertFalse(csvFormat.getAllowMissingColumnNames());
        assertEquals(QuoteMode.ALL_NON_NULL, csvFormat.getQuoteMode());

        assertEquals('\t', csvFormat.getDelimiter());
        assertFalse(csvFormat.getSkipHeaderRecord());

        assertEquals("\n", csvFormat.getRecordSeparator());
        assertFalse(csvFormat.getIgnoreEmptyLines());

        assertEquals('\"', (char)csvFormat.getQuoteCharacter());
        assertTrue(csvFormat.isNullStringSet());

        assertEquals('\\', (char)csvFormat.getEscapeCharacter());
        assertFalse(csvFormat.getIgnoreSurroundingSpaces());

        assertFalse(csvFormat.getTrailingDelimiter());
        assertFalse(csvFormat.getTrim());

        assertTrue(csvFormat.isQuoteCharacterSet());
        assertEquals("\\N", csvFormat.getNullString());

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertTrue(csvFormat.isEscapeCharacterSet());

        assertFalse(csvFormat.isCommentMarkerSet());
        assertNull(csvFormat.getCommentMarker());

        assertFalse(csvFormat.getAllowMissingColumnNames());
        assertEquals(QuoteMode.ALL_NON_NULL, csvFormat.getQuoteMode());

        assertEquals('\t', csvFormat.getDelimiter());
        assertFalse(csvFormat.getSkipHeaderRecord());

        assertEquals("\n", csvFormat.getRecordSeparator());
        assertFalse(csvFormat.getIgnoreEmptyLines());

        assertEquals('\"', (char)csvFormat.getQuoteCharacter());
        assertTrue(csvFormat.isNullStringSet());

        Assertions.assertNotEquals(null, csvFormat);

    }
}
