package org.apache.commons.csv.format;

import java.io.Serializable;

public interface ICSVFormat extends Serializable {
    boolean getAllowDuplicateHeaderNames();

    boolean getAllowMissingColumnNames();

    boolean getAutoFlush();

    Character getCommentMarker();

    @Deprecated
    char getDelimiter();

    String getDelimiterString();

    Character getEscapeCharacter();

    String[] getHeader();

    String[] getHeaderComments();

    boolean getIgnoreEmptyLines();

    boolean getIgnoreHeaderCase();

    boolean getIgnoreSurroundingSpaces();

    String getNullString();

    Character getQuoteCharacter();

    String getQuotedNullString();

    QuoteMode getQuoteMode();

    String getRecordSeparator();

    boolean getSkipHeaderRecord();

    boolean getTrailingDelimiter();

    boolean getTrim();

    ICSVFormat copy();

    boolean isCommentMarkerSet();

    boolean isEscapeCharacterSet();

    boolean isNullStringSet();

    boolean isQuoteCharacterSet();

    CSVFormatBuilder builder();
}
