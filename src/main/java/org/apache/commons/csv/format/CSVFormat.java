/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.csv.format;

import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.csv.record.CSVRecord;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Specifies the format of a CSV file and parses input.
 *
 * <h2>Using predefined formats</h2>
 *
 * <p>
 * You can use one of the predefined formats:
 * </p>
 *
 * <ul>
 * <li>{@link CSVFormatPredefinedFormats#Default}</li>
 * <li>{@link CSVFormatPredefinedFormats#Excel}</li>
 * <li>{@link CSVFormatPredefinedFormats#InformixUnload}</li>
 * <li>{@link CSVFormatPredefinedFormats#InformixUnloadCsv}</li>
 * <li>{@link CSVFormatPredefinedFormats#MySQL}</li>
 * <li>{@link CSVFormatPredefinedFormats#RFC4180}</li>
 * <li>{@link CSVFormatPredefinedFormats#Oracle}</li>
 * <li>{@link CSVFormatPredefinedFormats#PostgreSQLCsv}</li>
 * <li>{@link CSVFormatPredefinedFormats#PostgreSQLText}</li>
 * <li>{@link CSVFormatPredefinedFormats#TDF}</li>
 * </ul>
 *
 * <p>
 * For example:
 * </p>
 *
 * <pre>
 * CSVParser parser = CSVFormat.EXCEL.parse(reader);
 * </pre>
 *
 * <p>
 * The {@link CSVParser} provides static methods to parse other input types, for example:
 * </p>
 *
 * <pre>
 * CSVParser parser = CSVParser.parse(file, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
 * </pre>
 *
 * <h2>Defining formats</h2>
 *
 * <p>
 * You can extend a format by calling the {@code set} methods. For example:
 * </p>
 *
 * <pre>
 * CSVFormat.EXCEL.withNullString(&quot;N/A&quot;).withIgnoreSurroundingSpaces(true);
 * </pre>
 *
 * <h2>Defining column names</h2>
 *
 * <p>
 * To define the column names you want to use to access records, write:
 * </p>
 *
 * <pre>
 * CSVFormat.EXCEL.withHeader(&quot;Col1&quot;, &quot;Col2&quot;, &quot;Col3&quot;);
 * </pre>
 *
 * <p>
 * Calling {@link CSVFormatBuilder#setHeader(String...)} lets you use the given names to address values in a {@link CSVRecord}, and assumes that your CSV source does not
 * contain a first record that also defines column names.
 *
 * If it does, then you are overriding this metadata with your names and you should skip the first record by calling
 * {@link CSVFormatBuilder#setSkipHeaderRecord(boolean)} with {@code true}.
 * </p>
 *
 * <h2>Parsing</h2>
 *
 * <p>
 * You can use a format directly to parse a reader. For example, to parse an Excel file with columns header, write:
 * </p>
 *
 * <pre>
 * Reader in = ...;
 * CSVFormat.EXCEL.withHeader(&quot;Col1&quot;, &quot;Col2&quot;, &quot;Col3&quot;).parse(in);
 * </pre>
 *
 * <p>
 * For other input types, like resources, files, and URLs, use the static methods on {@link CSVParser}.
 * </p>
 *
 * <h2>Referencing columns safely</h2>
 *
 * <p>
 * If your source contains a header record, you can simplify your code and safely reference columns, by using {@link CSVFormatBuilder#setHeader(String...)} with no
 * arguments:
 * </p>
 *
 * <pre>
 * CSVFormat.EXCEL.withHeader();
 * </pre>
 *
 * <p>
 * This causes the parser to read the first record and use its values as column names.
 *
 * Then, call one of the {@link CSVRecord} get method that takes a String column name argument:
 * </p>
 *
 * <pre>
 * String value = record.get(&quot;Col1&quot;);
 * </pre>
 *
 * <p>
 * This makes your code impervious to changes in column order in the CSV file.
 * </p>
 *
 * <h2>Notes</h2>
 *
 * <p>
 * This class is immutable.
 * </p>
 */
public class CSVFormat implements ICSVFormat {

    private boolean allowDuplicateHeaderNames;

    private boolean allowMissingColumnNames;

    private boolean autoFlush;

    private Character commentMarker; // null if commenting is disabled

    private String delimiter;

    private Character escapeCharacter; // null if escaping is disabled

    private String[] header; // array of header column names

    private String[] headerComments; // array of header comment lines

    private boolean ignoreEmptyLines;

    private boolean ignoreHeaderCase; // should ignore header names case

    private boolean ignoreSurroundingSpaces; // Should leading/trailing spaces be ignored around values?

    private String nullString; // the string to be used for null values

    private Character quoteCharacter; // null if quoting is disabled

    private String quotedNullString;

    private QuoteMode quoteMode;

    private String recordSeparator; // for outputs

    private boolean skipHeaderRecord;

    private boolean trailingDelimiter;

    private boolean trim;

    /**
     * Returns true if and only if duplicate names are allowed in the headers.
     *
     * @return whether duplicate header names are allowed
     * @since 1.7
     */
    @Override
    public boolean getAllowDuplicateHeaderNames() {
        return allowDuplicateHeaderNames;
    }

    public void setAllowDuplicateHeaderNames(boolean allowDuplicateHeaderNames) {
        this.allowDuplicateHeaderNames = allowDuplicateHeaderNames;
    }

    /**
     * Specifies whether missing column names are allowed when parsing the header line.
     *
     * @return {@code true} if missing column names are allowed when parsing the header line, {@code false} to throw an {@link IllegalArgumentException}.
     */
    @Override
    public boolean getAllowMissingColumnNames() {
        return allowMissingColumnNames;
    }

    public void setAllowMissingColumnNames(boolean allowMissingColumnNames) {
        this.allowMissingColumnNames = allowMissingColumnNames;
    }

    /**
     * Returns whether to flush on close.
     *
     * @return whether to flush on close.
     * @since 1.6
     */
    @Override
    public boolean getAutoFlush() {
        return autoFlush;
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    /**
     * Returns the character marking the start of a line comment.
     *
     * @return the comment start marker, may be {@code null}
     */
    @Override
    public Character getCommentMarker() {
        return commentMarker;
    }

    public void setCommentMarker(Character commentMarker) {
        this.commentMarker = commentMarker;
    }

    /**
     * Returns the first character delimiting the values (typically ';', ',' or '\t').
     *
     * @return the first delimiter character.
     * @deprecated Use {@link #getDelimiterString()}.
     */
    @Override
    @Deprecated
    public char getDelimiter() {
        return delimiter.charAt(0);
    }

    /**
     * Returns the character delimiting the values (typically ";", "," or "\t").
     *
     * @return the delimiter.
     */
    @Override
    public String getDelimiterString() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Returns the escape character.
     *
     * @return the escape character, may be {@code null}
     */
    @Override
    public Character getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(Character escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    /**
     * Returns a copy of the header array.
     *
     * @return a copy of the header array; {@code null} if disabled, the empty array if to be read from the file
     */
    @Override
    public String[] getHeader() {
        return header != null ? header.clone() : null;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    /**
     * Returns a copy of the header comment array.
     *
     * @return a copy of the header comment array; {@code null} if disabled.
     */
    @Override
    public String[] getHeaderComments() {
        return headerComments != null ? headerComments.clone() : null;
    }

    public void setHeaderComments(String[] headerComments) {
        this.headerComments = headerComments;
    }



    /**
     * Specifies whether empty lines between records are ignored when parsing input.
     *
     * @return {@code true} if empty lines between records are ignored, {@code false} if they are turned into empty records.
     */
    @Override
    public boolean getIgnoreEmptyLines() {
        return ignoreEmptyLines;
    }

    public void setIgnoreEmptyLines(boolean ignoreEmptyLines) {
        this.ignoreEmptyLines = ignoreEmptyLines;
    }



    /**
     * Specifies whether header names will be accessed ignoring case.
     *
     * @return {@code true} if header names cases are ignored, {@code false} if they are case sensitive.
     * @since 1.3
     */
    @Override
    public boolean getIgnoreHeaderCase() {
        return ignoreHeaderCase;
    }

    public void setIgnoreHeaderCase(boolean ignoreHeaderCase) {
        this.ignoreHeaderCase = ignoreHeaderCase;
    }



    /**
     * Specifies whether spaces around values are ignored when parsing input.
     *
     * @return {@code true} if spaces around values are ignored, {@code false} if they are treated as part of the value.
     */
    @Override
    public boolean getIgnoreSurroundingSpaces() {
        return ignoreSurroundingSpaces;
    }

    public void setIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
    }

    /**
     * Gets the String to convert to and from {@code null}.
     * <ul>
     * <li><strong>Reading:</strong> Converts strings equal to the given {@code nullString} to {@code null} when reading records.</li>
     * <li><strong>Writing:</strong> Writes {@code null} as the given {@code nullString} when writing records.</li>
     * </ul>
     *
     * @return the String to convert to and from {@code null}. No substitution occurs if {@code null}
     */
    @Override
    public String getNullString() {
        return nullString;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    /**
     * Returns the character used to encapsulate values containing special characters.
     *
     * @return the quoteChar character, may be {@code null}
     */
    @Override
    public Character getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(Character quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    @Override
    public String getQuotedNullString() {
        return quotedNullString;
    }

    public void setQuotedNullString(String quotedNullString) {
        this.quotedNullString = quotedNullString;
    }

    /**
     * Returns the quote policy output fields.
     *
     * @return the quote policy
     */
    @Override
    public QuoteMode getQuoteMode() {
        return quoteMode;
    }

    public void setQuoteMode(QuoteMode quoteMode) {
        this.quoteMode = quoteMode;
    }

    /**
     * Returns the record separator delimiting output records.
     *
     * @return the record separator
     */
    @Override
    public String getRecordSeparator() {
        return recordSeparator;
    }

    public void setRecordSeparator(String recordSeparator) {
        this.recordSeparator = recordSeparator;
    }



    /**
     * Returns whether to skip the header record.
     *
     * @return whether to skip the header record.
     */
    @Override
    public boolean getSkipHeaderRecord() {
        return skipHeaderRecord;
    }

    public void setSkipHeaderRecord(boolean skipHeaderRecord) {
        this.skipHeaderRecord = skipHeaderRecord;
    }



    /**
     * Returns whether to add a trailing delimiter.
     *
     * @return whether to add a trailing delimiter.
     * @since 1.3
     */
    @Override
    public boolean getTrailingDelimiter() {
        return trailingDelimiter;
    }

    public void setTrailingDelimiter(boolean trailingDelimiter) {
        this.trailingDelimiter = trailingDelimiter;
    }

    /**
     * Returns whether to trim leading and trailing blanks.
     *
     * @return whether to trim leading and trailing blanks.
     */
    @Override
    public boolean getTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    private static final long serialVersionUID = 1L;


    /**
     * Creates a customized CSV format.
     *
     * @param delimiter               the char used for value separation, must not be a line break character.
     * @param quoteChar               the Character used as value encapsulation marker, may be {@code null} to disable.
     * @param quoteMode               the quote mode.
     * @param commentStart            the Character used for comment identification, may be {@code null} to disable.
     * @param escape                  the Character used to escape special characters in values, may be {@code null} to disable.
     * @param ignoreSurroundingSpaces {@code true} when whitespaces enclosing values should be ignored.
     * @param ignoreEmptyLines        {@code true} when the parser should skip empty lines.
     * @param recordSeparator         the line separator to use for output.
     * @param nullString              the line separator to use for output.
     * @param headerComments          the comments to be printed by the Printer before the actual CSV data.
     * @param header                  the header
     * @param skipHeaderRecord        TODO Doc me.
     * @param allowMissingColumnNames TODO Doc me.
     * @param ignoreHeaderCase        TODO Doc me.
     * @param trim                    TODO Doc me.
     * @param trailingDelimiter       TODO Doc me.
     * @param autoFlush               TODO Doc me.
     * @param allowDuplicateHeaderNames TODO Doc me.
     * @throws IllegalArgumentException if the delimiter is a line break character.
     */
    CSVFormat(final String delimiter, final Character quoteChar, final QuoteMode quoteMode, final Character commentStart, final Character escape,
              final boolean ignoreSurroundingSpaces, final boolean ignoreEmptyLines, final String recordSeparator, final String nullString,
              final Object[] headerComments, final String[] header, final boolean skipHeaderRecord, final boolean allowMissingColumnNames,
              final boolean ignoreHeaderCase, final boolean trim, final boolean trailingDelimiter, final boolean autoFlush,
              final boolean allowDuplicateHeaderNames) {
        this.setDelimiter(delimiter);
        this.setQuoteCharacter(quoteChar);
        this.setQuoteMode(quoteMode);
        this.setCommentMarker(commentStart);
        this.setEscapeCharacter(escape);
        this.setIgnoreSurroundingSpaces(ignoreSurroundingSpaces);
        this.setAllowMissingColumnNames(allowMissingColumnNames);
        this.setIgnoreEmptyLines(ignoreEmptyLines);
        this.setRecordSeparator(recordSeparator);
        this.setNullString(nullString);
        this.setHeaderComments(CSVFormatHelper.toStringArray(headerComments));
        this.setHeader(clone(header));
        this.setSkipHeaderRecord(skipHeaderRecord);
        this.setIgnoreHeaderCase(ignoreHeaderCase);
        this.setTrailingDelimiter(trailingDelimiter);
        this.setTrim(trim);
        this.setAutoFlush(autoFlush);
        this.setQuotedNullString(getQuoteCharacter() + nullString + getQuoteCharacter());
        this.setAllowDuplicateHeaderNames(allowDuplicateHeaderNames);
        validate();
    }

    /**
     * Creates a new Builder for this instance.
     *
     * @return a new Builder.
     */
    /*public CSVFormatBuilder builder() {
        return CSVFormatBuilder.create(this);
    }*/

    /**
     * Creates a copy of this instance.
     *
     * @return a copy of this instance.
     */
    public CSVFormat copy() {
        return SerializationUtils.clone(this);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CSVFormat other = (CSVFormat) obj;
        return getAllowDuplicateHeaderNames() == other.getAllowDuplicateHeaderNames() && getAllowMissingColumnNames() == other.getAllowMissingColumnNames() &&
                getAutoFlush() == other.getAutoFlush() && Objects.equals(getCommentMarker(), other.getCommentMarker()) && Objects.equals(getDelimiter(), other.getDelimiter()) &&
                Objects.equals(getEscapeCharacter(), other.getEscapeCharacter()) && Arrays.equals(getHeader(), other.getHeader()) &&
                Arrays.equals(getHeaderComments(), other.getHeaderComments()) && getIgnoreEmptyLines() == other.getIgnoreEmptyLines() &&
                getIgnoreHeaderCase() == other.getIgnoreHeaderCase() && getIgnoreSurroundingSpaces() == other.getIgnoreSurroundingSpaces() &&
                Objects.equals(getNullString(), other.getNullString()) && Objects.equals(getQuoteCharacter(), other.getQuoteCharacter()) && getQuoteMode() == other.getQuoteMode() &&
                Objects.equals(getQuotedNullString(), other.getQuotedNullString()) && Objects.equals(getRecordSeparator(), other.getRecordSeparator()) &&
                getSkipHeaderRecord() == other.getSkipHeaderRecord() && getTrailingDelimiter() == other.getTrailingDelimiter() && getTrim() == other.getTrim();
    }

    /**
     * Formats the specified values.
     *
     * @param values the values to format
     * @return the formatted values
     */
    public String format(final Object... values) {
        final StringWriter out = new StringWriter();
        try (CSVPrinter csvPrinter = new CSVPrinter(out, this)) {
            csvPrinter.printRecord(values);
            final String res = out.toString();
            final int len = getRecordSeparator() != null ? res.length() - getRecordSeparator().length() : res.length();
            return res.substring(0, len);
        } catch (final IOException e) {
            // should not happen because a StringWriter does not do IO.
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(getHeader());
        result = prime * result + Arrays.hashCode(getHeaderComments());
        return prime * result + Objects.hash(getAllowDuplicateHeaderNames(), getAllowMissingColumnNames(), getAutoFlush(), getCommentMarker(), getDelimiter(), getEscapeCharacter(),
                getIgnoreEmptyLines(), getIgnoreHeaderCase(), getIgnoreSurroundingSpaces(), getNullString(), getQuoteCharacter(), getQuoteMode(), getQuotedNullString(), getRecordSeparator(),
                getSkipHeaderRecord(), getTrailingDelimiter(), getTrim());
    }

    /**
     * Specifies whether comments are supported by this format.
     *
     * Note that the comment introducer character is only recognized at the start of a line.
     *
     * @return {@code true} is comments are supported, {@code false} otherwise
     */
    public boolean isCommentMarkerSet() {
        return getCommentMarker() != null;
    }

    /**
     * Returns whether escape are being processed.
     *
     * @return {@code true} if escapes are processed
     */
    public boolean isEscapeCharacterSet() {
        return getEscapeCharacter() != null;
    }

    /**
     * Returns whether a nullString has been defined.
     *
     * @return {@code true} if a nullString is defined
     */
    public boolean isNullStringSet() {
        return getNullString() != null;
    }

    /**
     * Returns whether a quoteChar has been defined.
     *
     * @return {@code true} if a quoteChar is defined
     */
    public boolean isQuoteCharacterSet() {
        return getQuoteCharacter() != null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Delimiter=<").append(getDelimiter()).append('>');
        if (isEscapeCharacterSet()) {
            sb.append(' ');
            sb.append("Escape=<").append(getEscapeCharacter()).append('>');
        }
        if (isQuoteCharacterSet()) {
            sb.append(' ');
            sb.append("QuoteChar=<").append(getQuoteCharacter()).append('>');
        }
        if (getQuoteMode() != null) {
            sb.append(' ');
            sb.append("QuoteMode=<").append(getQuoteMode()).append('>');
        }
        if (isCommentMarkerSet()) {
            sb.append(' ');
            sb.append("CommentStart=<").append(getCommentMarker()).append('>');
        }
        if (isNullStringSet()) {
            sb.append(' ');
            sb.append("NullString=<").append(getNullString()).append('>');
        }
        if (getRecordSeparator() != null) {
            sb.append(' ');
            sb.append("RecordSeparator=<").append(getRecordSeparator()).append('>');
        }
        if (getIgnoreEmptyLines()) {
            sb.append(" EmptyLines:ignored");
        }
        if (getIgnoreSurroundingSpaces()) {
            sb.append(" SurroundingSpaces:ignored");
        }
        if (getIgnoreHeaderCase()) {
            sb.append(" IgnoreHeaderCase:ignored");
        }
        sb.append(" SkipHeaderRecord:").append(getSkipHeaderRecord());
        if (getHeaderComments() != null) {
            sb.append(' ');
            sb.append("HeaderComments:").append(Arrays.toString(getHeaderComments()));
        }
        if (getHeader() != null) {
            sb.append(' ');
            sb.append("Header:").append(Arrays.toString(getHeader()));
        }
        return sb.toString();
    }

    /**
     * Verifies the validity and consistency of the attributes, and throws an IllegalArgumentException if necessary.
     *
     * @throws IllegalArgumentException Throw when any attribute is invalid or inconsistent with other attributes.
     */
    private void validate() throws IllegalArgumentException {
        if (CSVFormatHelper.containsLineBreak(getDelimiterString())) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }

        if (getQuoteCharacter() != null && CSVFormatHelper.contains(getDelimiterString(), getQuoteCharacter())) {
            throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + getQuoteCharacter() + "')");
        }

        if (getEscapeCharacter() != null && CSVFormatHelper.contains(getDelimiterString(), getEscapeCharacter())) {
            throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + getEscapeCharacter() + "')");
        }

        if (getCommentMarker() != null && CSVFormatHelper.contains(getDelimiterString(), getCommentMarker())) {
            throw new IllegalArgumentException("The comment start character and the delimiter cannot be the same ('" + getCommentMarker() + "')");
        }

        if (getQuoteCharacter() != null && getQuoteCharacter().equals(getCommentMarker())) {
            throw new IllegalArgumentException("The comment start character and the quoteChar cannot be the same ('" + getCommentMarker() + "')");
        }

        if (getEscapeCharacter() != null && getEscapeCharacter().equals(getCommentMarker())) {
            throw new IllegalArgumentException("The comment start and the escape character cannot be the same ('" + getCommentMarker() + "')");
        }

        if (getEscapeCharacter() == null && getQuoteMode() == QuoteMode.NONE) {
            throw new IllegalArgumentException("No quotes mode set but no escape character is set");
        }

        // validate header
        if (getHeader() != null && !getAllowDuplicateHeaderNames()) {
            final Set<String> dupCheck = new HashSet<>();
            for (final String hdr : getHeader()) {
                if (!dupCheck.add(hdr)) {
                    throw new IllegalArgumentException("The header contains a duplicate entry: '" + hdr + "' in " + Arrays.toString(getHeader()));
                }
            }
        }
    }

    /**
     * Null-safe clone of an array.
     *
     * @param <T>    The array element type.
     * @param values the source array
     * @return the cloned array.
     */
    @SafeVarargs
    static <T> T[] clone(final T... values) {
        return values == null ? null : values.clone();
    }

    /**
     * Creates a new CSV format with the specified delimiter.
     *
     * <p>
     * Use this method if you want to create a CSVFormat from scratch. All fields but the delimiter will be initialized with null/false.
     * </p>
     *
     * @param delimiter the char used for value separation, must not be a line break character
     * @return a new CSV format.
     * @throws IllegalArgumentException if the delimiter is a line break character
     *
     */
    public static CSVFormat newFormat(final char delimiter) {
        return new CSVFormat(String.valueOf(delimiter), null, null, null, null, false, false, null, null, null, null, false, false, false, false, false, false,
                true);
    }

    /**
     * Gets one of the predefined formats from {@link CSVFormatPredefinedFormats}.
     *
     * @param format name
     * @return one of the predefined formats
     * @since 1.2
     */
    public static CSVFormat valueOf(final String format) {
        return CSVFormatPredefinedFormats.valueOf(format).getFormat();
    }
}
