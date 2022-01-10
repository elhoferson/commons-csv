package org.apache.commons.csv.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSVFormatCommentTest {

    @Test
    public void testEscapeSameAsCommentStartThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setEscape('!').setCommentMarker('!').build());
    }

    @Test
    public void testEscapeSameAsCommentStartThrowsExceptionForWrapperType() {
        // Cannot assume that callers won't use different Character objects
        assertThrows(
                IllegalArgumentException.class,
                () -> new CSVFormatBuilder().setEscape(Character.valueOf('!')).setCommentMarker(Character.valueOf('!')).build());
    }
}
