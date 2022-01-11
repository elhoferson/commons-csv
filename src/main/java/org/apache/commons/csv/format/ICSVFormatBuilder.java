package org.apache.commons.csv.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface ICSVFormatBuilder {
    boolean isAllowDuplicateHeaderNames();

    boolean isAllowMissingColumnNames();

    boolean isAutoFlush();

    Character getCommentMarker();

    String getDelimiter();

    Character getEscapeCharacter();

    String[] getHeaderComments();

    String[] getHeaders();

    CSVFormatBuilder setHeaders(String[] headers);

    boolean isIgnoreEmptyLines();

    boolean isIgnoreHeaderCase();

    boolean isIgnoreSurroundingSpaces();

    String getNullString();

    Character getQuoteCharacter();

    String getQuotedNullString();

    QuoteMode getQuoteMode();

    String getRecordSeparator();

    boolean isSkipHeaderRecord();

    boolean isTrailingDelimiter();

    boolean isTrim();

    CSVFormat build();

    CSVFormatBuilder setAllowDuplicateHeaderNames(boolean allowDuplicateHeaderNames);

    CSVFormatBuilder setAllowMissingColumnNames(boolean allowMissingColumnNames);

    CSVFormatBuilder setAutoFlush(boolean autoFlush);

    CSVFormatBuilder setCommentMarker(char commentMarker);

    CSVFormatBuilder setCommentMarker(Character commentMarker);

    CSVFormatBuilder setDelimiter(char delimiter);

    CSVFormatBuilder setDelimiter(String delimiter);

    CSVFormatBuilder setEscape(char escapeCharacter);

    CSVFormatBuilder setEscape(Character escapeCharacter);

    CSVFormatBuilder setHeader(Class<? extends Enum<?>> headerEnum);

    CSVFormatBuilder setHeader(ResultSet resultSet) throws SQLException;

    CSVFormatBuilder setHeader(ResultSetMetaData resultSetMetaData) throws SQLException;

    CSVFormatBuilder setHeader(String... header);

    CSVFormatBuilder setHeaderComments(Object... headerComments);

    CSVFormatBuilder setHeaderComments(String... headerComments);

    CSVFormatBuilder setIgnoreEmptyLines(boolean ignoreEmptyLines);

    CSVFormatBuilder setIgnoreHeaderCase(boolean ignoreHeaderCase);

    CSVFormatBuilder setIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces);

    CSVFormatBuilder setNullString(String nullString);

    CSVFormatBuilder setQuote(char quoteCharacter);

    CSVFormatBuilder setQuote(Character quoteCharacter);

    CSVFormatBuilder setQuoteMode(QuoteMode quoteMode);

    CSVFormatBuilder setRecordSeparator(char recordSeparator);

    CSVFormatBuilder setRecordSeparator(String recordSeparator);

    CSVFormatBuilder setSkipHeaderRecord(boolean skipHeaderRecord);

    CSVFormatBuilder setTrailingDelimiter(boolean trailingDelimiter);

    CSVFormatBuilder setTrim(boolean trim);
}
