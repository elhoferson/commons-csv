package org.apache.commons.csv.format;

import org.apache.commons.csv.Constants;
import org.apache.commons.csv.printer.CSVPrinter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Builds CSVFormat instances.
 *
 * @since 1.9.0
 */
public class CSVFormatBuilder {

    /**
     * Creates a new default builder.
     *
     * @return a copy of the builder
     */
    //public static CSVFormatBuilder create() {
    //    return new CSVFormatBuilder(CSVFormat.DEFAULT);
    //}

    /**
     * Creates a new builder for the given format.
     *
     * @param csvFormat the source format.
     * @return a copy of the builder
     */
    //public static CSVFormatBuilder create(final CSVFormat csvFormat) {
    //    return new CSVFormatBuilder(csvFormat);
    //}

    private boolean allowDuplicateHeaderNames;

    private boolean allowMissingColumnNames;

    private boolean autoFlush;

    private Character commentMarker;

    private String delimiter;

    private Character escapeCharacter;

    private String[] headerComments;

    private String[] headers;

    private boolean ignoreEmptyLines;

    private boolean ignoreHeaderCase;

    private boolean ignoreSurroundingSpaces;

    private String nullString;

    private Character quoteCharacter;

    private String quotedNullString;

    private QuoteMode quoteMode;

    private String recordSeparator;

    private boolean skipHeaderRecord;

    public boolean isAllowDuplicateHeaderNames() {
        return allowDuplicateHeaderNames;
    }

    public boolean isAllowMissingColumnNames() {
        return allowMissingColumnNames;
    }

    public boolean isAutoFlush() {
        return autoFlush;
    }

    public Character getCommentMarker() {
        return commentMarker;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public Character getEscapeCharacter() {
        return escapeCharacter;
    }

    public String[] getHeaderComments() {
        return headerComments;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public boolean isIgnoreEmptyLines() {
        return ignoreEmptyLines;
    }

    public boolean isIgnoreHeaderCase() {
        return ignoreHeaderCase;
    }

    public boolean isIgnoreSurroundingSpaces() {
        return ignoreSurroundingSpaces;
    }

    public String getNullString() {
        return nullString;
    }

    public Character getQuoteCharacter() {
        return quoteCharacter;
    }

    public String getQuotedNullString() {
        return quotedNullString;
    }

    public QuoteMode getQuoteMode() {
        return quoteMode;
    }

    public String getRecordSeparator() {
        return recordSeparator;
    }

    public boolean isSkipHeaderRecord() {
        return skipHeaderRecord;
    }

    public boolean isTrailingDelimiter() {
        return trailingDelimiter;
    }

    public boolean isTrim() {
        return trim;
    }

    private boolean trailingDelimiter;

    private boolean trim;

    public CSVFormatBuilder() {
        this.delimiter = Constants.COMMA;
        this.quoteCharacter = Constants.DOUBLE_QUOTE_CHAR;
        this.quoteMode = null;
        this.commentMarker = null;
        this.escapeCharacter = null;
        this.ignoreSurroundingSpaces = false;
        this.ignoreEmptyLines = true;
        this.recordSeparator = Constants.CRLF;
        this.nullString = null;
        this.headerComments = null;
        this.headers = null;
        this.skipHeaderRecord = false;
        this.allowMissingColumnNames = false;
        this.ignoreHeaderCase = false;
        this.trim = false;
        this.trailingDelimiter = false;
        this.autoFlush = false;
        this.allowDuplicateHeaderNames = true;
    }



    /**
     * Builds a new CSVFormat instance.
     *
     * @return a new CSVFormat instance.
     */
    public CSVFormat build() {
        return new CSVFormat(this.getDelimiter(),this.getQuoteCharacter(),
                this.getQuoteMode(),this.getCommentMarker(),
                this.getEscapeCharacter(),this.isIgnoreSurroundingSpaces(),
                this.isIgnoreEmptyLines(),
                this.getRecordSeparator(),this.getNullString(),
                this.getHeaderComments(),this.getHeaders(),
                this.isSkipHeaderRecord(),
                this.isAllowMissingColumnNames(), this.isIgnoreHeaderCase(),
                this.isTrim(), this.isTrailingDelimiter(),
                this.isAutoFlush(),
                this.isAllowDuplicateHeaderNames());
    }

    /**
     * Sets the duplicate header names behavior, true to allow, false to disallow.
     *
     * @param allowDuplicateHeaderNames the duplicate header names behavior, true to allow, false to disallow.
     * @return This instance.
     */
    public CSVFormatBuilder setAllowDuplicateHeaderNames(final boolean allowDuplicateHeaderNames) {
        this.allowDuplicateHeaderNames = allowDuplicateHeaderNames;
        return this;
    }

    /**
     * Sets the missing column names behavior, {@code true} to allow missing column names in the header line, {@code false} to cause an
     * {@link IllegalArgumentException} to be thrown.
     *
     * @param allowMissingColumnNames the missing column names behavior, {@code true} to allow missing column names in the header line, {@code false} to
     *                                cause an {@link IllegalArgumentException} to be thrown.
     * @return This instance.
     */
    public CSVFormatBuilder setAllowMissingColumnNames(final boolean allowMissingColumnNames) {
        this.allowMissingColumnNames = allowMissingColumnNames;
        return this;
    }

    /**
     * Sets whether to flush on close.
     *
     * @param autoFlush whether to flush on close.
     * @return This instance.
     */
    public CSVFormatBuilder setAutoFlush(final boolean autoFlush) {
        this.autoFlush = autoFlush;
        return this;
    }

    /**
     * Sets the comment start marker, use {@code null} to disable.
     *
     * Note that the comment start character is only recognized at the start of a line.
     *
     * @param commentMarker the comment start marker, use {@code null} to disable.
     * @return This instance.
     * @throws IllegalArgumentException thrown if the specified character is a line break
     */
    public CSVFormatBuilder setCommentMarker(final char commentMarker) {
        setCommentMarker(Character.valueOf(commentMarker));
        return this;
    }

    /**
     * Sets the comment start marker, use {@code null} to disable.
     *
     * Note that the comment start character is only recognized at the start of a line.
     *
     * @param commentMarker the comment start marker, use {@code null} to disable.
     * @return This instance.
     * @throws IllegalArgumentException thrown if the specified character is a line break
     */
    public CSVFormatBuilder setCommentMarker(final Character commentMarker) {
        if (CSVFormatHelper.isLineBreak(commentMarker)) {
            throw new IllegalArgumentException("The comment start marker character cannot be a line break");
        }
        this.commentMarker = commentMarker;
        return this;
    }

    /**
     * Sets the delimiter character.
     *
     * @param delimiter the delimiter character.
     * @return This instance.
     */
    public CSVFormatBuilder setDelimiter(final char delimiter) {
        return setDelimiter(String.valueOf(delimiter));
    }

    /**
     * Sets the delimiter character.
     *
     * @param delimiter the delimiter character.
     * @return This instance.
     */
    public CSVFormatBuilder setDelimiter(final String delimiter) {
        if (CSVFormatHelper.containsLineBreak(delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }
        this.delimiter = delimiter;
        return this;
    }

    /**
     * Sets the escape character.
     *
     * @param escapeCharacter the escape character.
     * @return This instance.
     * @throws IllegalArgumentException thrown if the specified character is a line break
     */
    public CSVFormatBuilder setEscape(final char escapeCharacter) {
        setEscape(Character.valueOf(escapeCharacter));
        return this;
    }

    /**
     * Sets the escape character.
     *
     * @param escapeCharacter the escape character.
     * @return This instance.
     * @throws IllegalArgumentException thrown if the specified character is a line break
     */
    public CSVFormatBuilder setEscape(final Character escapeCharacter) {
        if (CSVFormatHelper.isLineBreak(escapeCharacter)) {
            throw new IllegalArgumentException("The escape character cannot be a line break");
        }
        this.escapeCharacter = escapeCharacter;
        return this;
    }

    /**
     * Sets the header defined by the given {@link Enum} class.
     *
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * public enum HeaderEnum {
     *     Name, Email, Phone
     * }
     *
     * Builder builder = builder.setHeader(HeaderEnum.class);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param headerEnum the enum defining the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return This instance.
     */
    public CSVFormatBuilder setHeader(final Class<? extends Enum<?>> headerEnum) {
        String[] header = null;
        if (headerEnum != null) {
            final Enum<?>[] enumValues = headerEnum.getEnumConstants();
            header = new String[enumValues.length];
            for (int i = 0; i < enumValues.length; i++) {
                header[i] = enumValues[i].name();
            }
        }
        return setHeader(header);
    }

    /**
     * Sets the header from the result set metadata. The header can either be parsed automatically from the input file with:
     *
     * <pre>
     * builder.setHeader();
     * </pre>
     *
     * or specified manually with:
     *
     * <pre>
     * builder.setHeader(resultSet);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param resultSet the resultSet for the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return This instance.
     * @throws SQLException SQLException if a database access error occurs or this method is called on a closed result set.
     */
    public CSVFormatBuilder setHeader(final ResultSet resultSet) throws SQLException {
        return setHeader(resultSet != null ? resultSet.getMetaData() : null);
    }

    /**
     * Sets the header from the result set metadata. The header can either be parsed automatically from the input file with:
     *
     * <pre>
     * builder.setHeader();
     * </pre>
     *
     * or specified manually with:
     *
     * <pre>
     * builder.setHeader(resultSetMetaData);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param resultSetMetaData the metaData for the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return This instance.
     * @throws SQLException SQLException if a database access error occurs or this method is called on a closed result set.
     */
    public CSVFormatBuilder setHeader(final ResultSetMetaData resultSetMetaData) throws SQLException {
        String[] labels = null;
        if (resultSetMetaData != null) {
            final int columnCount = resultSetMetaData.getColumnCount();
            labels = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                labels[i] = resultSetMetaData.getColumnLabel(i + 1);
            }
        }
        return setHeader(labels);
    }

    /**
     * Sets the header to the given values. The header can either be parsed automatically from the input file with:
     *
     * <pre>
     * builder.setHeader();
     * </pre>
     *
     * or specified manually with:
     *
     * <pre>
     * builder.setHeader(&quot;name&quot;, &quot;email&quot;, &quot;phone&quot;);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param header the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return This instance.
     */
    public CSVFormatBuilder setHeader(final String... header) {
        this.headers = CSVFormat.clone(header);
        return this;
    }

    /**
     * Sets the header comments set to the given values. The comments will be printed first, before the headers. This setting is ignored by the parser.
     *
     * <pre>
     * builder.setHeaderComments(&quot;Generated by Apache Commons CSV.&quot;, Instant.now());
     * </pre>
     *
     * @param headerComments the headerComments which will be printed by the Printer before the actual CSV data.
     * @return This instance.
     */
    public CSVFormatBuilder setHeaderComments(final Object... headerComments) {
        this.headerComments = CSVFormat.clone(CSVFormatHelper.toStringArray(headerComments));
        return this;
    }

    /**
     * Sets the header comments set to the given values. The comments will be printed first, before the headers. This setting is ignored by the parser.
     *
     * <pre>
     * Builder.setHeaderComments(&quot;Generated by Apache Commons CSV.&quot;, Instant.now());
     * </pre>
     *
     * @param headerComments the headerComments which will be printed by the Printer before the actual CSV data.
     * @return This instance.
     */
    public CSVFormatBuilder setHeaderComments(final String... headerComments) {
        this.headerComments = CSVFormat.clone(headerComments);
        return this;
    }

    /**
     * Sets the empty line skipping behavior, {@code true} to ignore the empty lines between the records, {@code false} to translate empty lines to empty
     * records.
     *
     * @param ignoreEmptyLines the empty line skipping behavior, {@code true} to ignore the empty lines between the records, {@code false} to translate
     *                         empty lines to empty records.
     * @return This instance.
     */
    public CSVFormatBuilder setIgnoreEmptyLines(final boolean ignoreEmptyLines) {
        this.ignoreEmptyLines = ignoreEmptyLines;
        return this;
    }

    /**
     * Sets the case mapping behavior, {@code true} to access name/values, {@code false} to leave the mapping as is.
     *
     * @param ignoreHeaderCase the case mapping behavior, {@code true} to access name/values, {@code false} to leave the mapping as is.
     * @return This instance.
     */
    public CSVFormatBuilder setIgnoreHeaderCase(final boolean ignoreHeaderCase) {
        this.ignoreHeaderCase = ignoreHeaderCase;
        return this;
    }

    /**
     * Sets the parser trimming behavior, {@code true} to remove the surrounding spaces, {@code false} to leave the spaces as is.
     *
     * @param ignoreSurroundingSpaces the parser trimming behavior, {@code true} to remove the surrounding spaces, {@code false} to leave the spaces as is.
     * @return This instance.
     */
    public CSVFormatBuilder setIgnoreSurroundingSpaces(final boolean ignoreSurroundingSpaces) {
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
        return this;
    }

    /**
     * Sets the String to convert to and from {@code null}. No substitution occurs if {@code null}.
     *
     * <ul>
     * <li><strong>Reading:</strong> Converts strings equal to the given {@code nullString} to {@code null} when reading records.</li>
     * <li><strong>Writing:</strong> Writes {@code null} as the given {@code nullString} when writing records.</li>
     * </ul>
     *
     * @param nullString the String to convert to and from {@code null}. No substitution occurs if {@code null}.
     * @return This instance.
     */
    public CSVFormatBuilder setNullString(final String nullString) {
        this.nullString = nullString;
        this.quotedNullString = quoteCharacter + nullString + quoteCharacter;
        return this;
    }

    /**
     * Sets the quote character.
     *
     * @param quoteCharacter the quote character.
     * @return This instance.
     */
    public CSVFormatBuilder setQuote(final char quoteCharacter) {
        setQuote(Character.valueOf(quoteCharacter));
        return this;
    }

    /**
     * Sets the quote character, use {@code null} to disable.
     *
     * @param quoteCharacter the quote character, use {@code null} to disable.
     * @return This instance.
     */
    public CSVFormatBuilder setQuote(final Character quoteCharacter) {
        if (CSVFormatHelper.isLineBreak(quoteCharacter)) {
            throw new IllegalArgumentException("The quoteChar cannot be a line break");
        }
        this.quoteCharacter = quoteCharacter;
        return this;
    }

    /**
     * Sets the quote policy to use for output.
     *
     * @param quoteMode the quote policy to use for output.
     * @return This instance.
     */
    public CSVFormatBuilder setQuoteMode(final QuoteMode quoteMode) {
        this.quoteMode = quoteMode;
        return this;
    }

    /**
     * Sets the record separator to use for output.
     *
     * <p>
     * <strong>Note:</strong> This setting is only used during printing and does not affect parsing. Parsing currently only works for inputs with '\n', '\r'
     * and "\r\n"
     * </p>
     *
     * @param recordSeparator the record separator to use for output.
     * @return This instance.
     */
    public CSVFormatBuilder setRecordSeparator(final char recordSeparator) {
        this.recordSeparator = String.valueOf(recordSeparator);
        return this;
    }

    /**
     * Sets the record separator to use for output.
     *
     * <p>
     * <strong>Note:</strong> This setting is only used during printing and does not affect parsing. Parsing currently only works for inputs with '\n', '\r'
     * and "\r\n"
     * </p>
     *
     * @param recordSeparator the record separator to use for output.
     * @return This instance.
     */
    public CSVFormatBuilder setRecordSeparator(final String recordSeparator) {
        this.recordSeparator = recordSeparator;
        return this;
    }

    /**
     * Sets whether to skip the header record.
     *
     * @param skipHeaderRecord whether to skip the header record.
     * @return This instance.
     */
    public CSVFormatBuilder setSkipHeaderRecord(final boolean skipHeaderRecord) {
        this.skipHeaderRecord = skipHeaderRecord;
        return this;
    }

    /**
     * Sets whether to add a trailing delimiter.
     *
     * @param trailingDelimiter whether to add a trailing delimiter.
     * @return This instance.
     */
    public CSVFormatBuilder setTrailingDelimiter(final boolean trailingDelimiter) {
        this.trailingDelimiter = trailingDelimiter;
        return this;
    }

    /**
     * Sets whether to trim leading and trailing blanks.
     *
     * @param trim whether to trim leading and trailing blanks.
     * @return This instance.
     */
    public CSVFormatBuilder setTrim(final boolean trim) {
        this.trim = trim;
        return this;
    }
}