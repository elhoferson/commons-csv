package org.apache.commons.csv.format;

import java.util.Objects;

import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.LF;

public class CSVFormatHelper {
    /**
     * Returns true if the given string contains the search char.
     *
     * @param source the string to check.
     * @param searchCh the character to search.
     *
     * @return true if {@code c} contains a line break character
     */
    public static boolean contains(final String source, final char searchCh) {
        return Objects.requireNonNull(source, "source").indexOf(searchCh) >= 0;
    }

    /**
     * Returns true if the given string contains a line break character.
     *
     * @param source the string to check.
     *
     * @return true if {@code c} contains a line break character.
     */
    public static boolean containsLineBreak(final String source) {
        return contains(source, CR) || contains(source, LF);
    }

    /**
     * Returns true if the given character is a line break character.
     *
     * @param c the character to check.
     *
     * @return true if {@code c} is a line break character.
     */
    public static boolean isLineBreak(final char c) {
        return c == LF || c == CR;
    }

    /**
     * Returns true if the given character is a line break character.
     *
     * @param c the character to check, may be null.
     *
     * @return true if {@code c} is a line break character (and not null).
     */
    public static boolean isLineBreak(final Character c) {
        return c != null && isLineBreak(c.charValue());
    }

    public static String[] toStringArray(final Object[] values) {
        if (values == null) {
            return null;
        }
        final String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = Objects.toString(values[i], null);
        }
        return strings;
    }
}
