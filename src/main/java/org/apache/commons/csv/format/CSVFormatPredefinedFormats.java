package org.apache.commons.csv.format;

import static org.apache.commons.csv.Constants.*;

/**
 * Predefines formats.
 *
 * @since 1.2
 */
public enum CSVFormatPredefinedFormats {

    Default(new CSVFormatBuilder().build()),

    Excel(new CSVFormatBuilder().setIgnoreEmptyLines(false)
            .setAllowMissingColumnNames(true)
            .build()),

    InformixUnload(new CSVFormatBuilder().setDelimiter(PIPE)
            .setEscape(BACKSLASH)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .build()),

    InformixUnloadCsv(new CSVFormatBuilder().setDelimiter(COMMA)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .build()),

    MongoDBCsv(new CSVFormatBuilder().setDelimiter(COMMA)
            .setEscape(DOUBLE_QUOTE_CHAR)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setQuoteMode(QuoteMode.MINIMAL)
            .setSkipHeaderRecord(false)
            .build()),

    MongoDBTsv(new CSVFormatBuilder().setDelimiter(TAB)
            .setEscape(DOUBLE_QUOTE_CHAR)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setQuoteMode(QuoteMode.MINIMAL)
            .setSkipHeaderRecord(false)
            .build()),

    MySQL(new CSVFormatBuilder().setDelimiter(TAB)
            .setEscape(BACKSLASH)
            .setIgnoreEmptyLines(false)
            .setQuote(null)
            .setRecordSeparator(LF)
            .setNullString("\\N")
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .build()),

    Oracle(new CSVFormatBuilder().setDelimiter(COMMA)
            .setEscape(BACKSLASH)
            .setIgnoreEmptyLines(false)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setNullString("\\N")
            .setTrim(true)
            .setRecordSeparator(System.lineSeparator())
            .setQuoteMode(QuoteMode.MINIMAL)
            .build()),

    PostgreSQLCsv(new CSVFormatBuilder().setDelimiter(COMMA)
            .setEscape(DOUBLE_QUOTE_CHAR)
            .setIgnoreEmptyLines(false)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .setNullString(EMPTY)
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .build()),

    PostgreSQLText(new CSVFormatBuilder().setDelimiter(TAB)
            .setEscape(BACKSLASH)
            .setIgnoreEmptyLines(false)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .setNullString("\\N")
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .build()),

    RFC4180(new CSVFormatBuilder().setIgnoreEmptyLines(false).build()),

    TDF(new CSVFormatBuilder().setDelimiter(TAB)
            .setIgnoreSurroundingSpaces(true)
            .build());

    private final CSVFormat format;

    CSVFormatPredefinedFormats(final CSVFormat format) {
        this.format = format;
    }

    /**
     * Gets the format.
     *
     * @return the format.
     */
    public CSVFormat getFormat() {
        return format.copy();
    }
}
