package org.apache.commons.csv.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSVFormatDelimiterTest {

    @Test
    public void testDelimiterSameAsCommentStartThrowsException1() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setDelimiter('!').setCommentMarker('!').build());
    }
    @Test
    public void testDelimiterSameAsEscapeThrowsException1() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setDelimiter('!').setEscape('!').build());
    }


}
