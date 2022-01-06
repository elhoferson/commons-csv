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

import org.apache.commons.csv.*;

import static org.apache.commons.csv.Constants.BACKSLASH;
import static org.apache.commons.csv.Constants.COMMA;
import static org.apache.commons.csv.Constants.COMMENT;
import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.CRLF;
import static org.apache.commons.csv.Constants.DOUBLE_QUOTE_CHAR;
import static org.apache.commons.csv.Constants.EMPTY;
import static org.apache.commons.csv.Constants.LF;
import static org.apache.commons.csv.Constants.PIPE;
import static org.apache.commons.csv.Constants.SP;
import static org.apache.commons.csv.Constants.TAB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
 * <li>{@link #DEFAULT}</li>
 * <li>{@link #EXCEL}</li>
 * <li>{@link #INFORMIX_UNLOAD}</li>
 * <li>{@link #INFORMIX_UNLOAD_CSV}</li>
 * <li>{@link #MYSQL}</li>
 * <li>{@link #RFC4180}</li>
 * <li>{@link #ORACLE}</li>
 * <li>{@link #POSTGRESQL_CSV}</li>
 * <li>{@link #POSTGRESQL_TEXT}</li>
 * <li>{@link #TDF}</li>
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
public final class CSVFormat implements Serializable {

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
    @Deprecated
    public char getDelimiter() {
        return delimiter.charAt(0);
    }

    /**
     * Returns the character delimiting the values (typically ";", "," or "\t").
     *
     * @return the delimiter.
     */
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
    public Character getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(Character quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

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
    public boolean getTrailingDelimiter() {
        return trailingDelimiter;
    }

    public void setTrailingDelimiter(boolean trailingDelimiter) {
        this.trailingDelimiter = trailingDelimiter;
    }

    /**
     * Returns whether to trim leading and trailing blanks. This is used by {@link #print(Object, Appendable, boolean)} Also by
     * {CSVParser#addRecordValue(boolean)}
     *
     * @return whether to trim leading and trailing blanks.
     */
    public boolean getTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * Standard Comma Separated Value format, as for {@link #RFC4180} but allowing empty lines.
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setRecordSeparator("\r\n")}</li>
     * <li>{@code setIgnoreEmptyLines(true)}</li>
     * <li>{@code setAllowDuplicateHeaderNames(true)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#Default
     */
    public static final CSVFormat DEFAULT = new CSVFormat(COMMA, DOUBLE_QUOTE_CHAR, null, null, null, false, true, CRLF, null, null, null, false, false, false,
            false, false, false, true);

    /**
     * Excel file format (using a comma as the value delimiter). Note that the actual value delimiter used by Excel is locale dependent, it might be necessary
     * to customize this format to accommodate to your regional settings.
     *
     * <p>
     * For example for parsing or generating a CSV file on a French system the following format will be used:
     * </p>
     *
     * <pre>
     * CSVFormat fmt = CSVFormat.EXCEL.withDelimiter(';');
     * </pre>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setRecordSeparator("\r\n")}</li>
     * <li>{@code setIgnoreEmptyLines(false)}</li>
     * <li>{@code setAllowMissingColumnNames(true)}</li>
     * <li>{@code setAllowDuplicateHeaderNames(true)}</li>
     * </ul>
     * <p>
     * Note: This is currently like {@link #RFC4180} plus {@link CSVFormatBuilder#setAllowMissingColumnNames(boolean) Builder#setAllowMissingColumnNames(true)} and
     * {@link CSVFormatBuilder#setIgnoreEmptyLines(boolean) Builder#setIgnoreEmptyLines(false)}.
     * </p>
     *
     * @see CSVFormatPredefinedFormats#Excel
     */
    // @formatter:off
    public static final CSVFormat EXCEL = DEFAULT.builder()
            .setIgnoreEmptyLines(false)
            .setAllowMissingColumnNames(true)
            .build();
    // @formatter:on

    /**
     * Default Informix CSV UNLOAD format used by the {@code UNLOAD TO file_name} operation.
     *
     * <p>
     * This is a comma-delimited format with a LF character as the line separator. Values are not quoted and special characters are escaped with {@code '\'}.
     * The default NULL string is {@code "\\N"}.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setEscape('\\')}</li>
     * <li>{@code setQuote("\"")}</li>
     * <li>{@code setRecordSeparator('\n')}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MySQL
     * @see <a href= "http://www.ibm.com/support/knowledgecenter/SSBJG3_2.5.0/com.ibm.gen_busug.doc/c_fgl_InOutSql_UNLOAD.htm">
     *      http://www.ibm.com/support/knowledgecenter/SSBJG3_2.5.0/com.ibm.gen_busug.doc/c_fgl_InOutSql_UNLOAD.htm</a>
     * @since 1.3
     */
    // @formatter:off
    public static final CSVFormat INFORMIX_UNLOAD = DEFAULT.builder()
            .setDelimiter(PIPE)
            .setEscape(BACKSLASH)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .build();
    // @formatter:on

    /**
     * Default Informix CSV UNLOAD format used by the {@code UNLOAD TO file_name} operation (escaping is disabled.)
     *
     * <p>
     * This is a comma-delimited format with a LF character as the line separator. Values are not quoted and special characters are escaped with {@code '\'}.
     * The default NULL string is {@code "\\N"}.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setQuote("\"")}</li>
     * <li>{@code setRecordSeparator('\n')}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MySQL
     * @see <a href= "http://www.ibm.com/support/knowledgecenter/SSBJG3_2.5.0/com.ibm.gen_busug.doc/c_fgl_InOutSql_UNLOAD.htm">
     *      http://www.ibm.com/support/knowledgecenter/SSBJG3_2.5.0/com.ibm.gen_busug.doc/c_fgl_InOutSql_UNLOAD.htm</a>
     * @since 1.3
     */
    // @formatter:off
    public static final CSVFormat INFORMIX_UNLOAD_CSV = DEFAULT.builder()
            .setDelimiter(COMMA)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .build();
    // @formatter:on

    /**
     * Default MongoDB CSV format used by the {@code mongoexport} operation.
     * <p>
     * <b>Parsing is not supported yet.</b>
     * </p>
     *
     * <p>
     * This is a comma-delimited format. Values are double quoted only if needed and special characters are escaped with {@code '"'}. A header line with field
     * names is expected.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setEscape('"')}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setQuoteMode(QuoteMode.ALL_NON_NULL)}</li>
     * <li>{@code setSkipHeaderRecord(false)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MongoDBCsv
     * @see <a href="https://docs.mongodb.com/manual/reference/program/mongoexport/">MongoDB mongoexport command documentation</a>
     * @since 1.7
     */
    // @formatter:off
    public static final CSVFormat MONGODB_CSV = DEFAULT.builder()
            .setDelimiter(COMMA)
            .setEscape(DOUBLE_QUOTE_CHAR)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setQuoteMode(QuoteMode.MINIMAL)
            .setSkipHeaderRecord(false)
            .build();
    // @formatter:off

    /**
     * Default MongoDB TSV format used by the {@code mongoexport} operation.
     * <p>
     * <b>Parsing is not supported yet.</b>
     * </p>
     *
     * <p>
     * This is a tab-delimited format. Values are double quoted only if needed and special
     * characters are escaped with {@code '"'}. A header line with field names is expected.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter('\t')}</li>
     * <li>{@code setEscape('"')}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setQuoteMode(QuoteMode.ALL_NON_NULL)}</li>
     * <li>{@code setSkipHeaderRecord(false)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MongoDBCsv
     * @see <a href="https://docs.mongodb.com/manual/reference/program/mongoexport/">MongoDB mongoexport command
     *          documentation</a>
     * @since 1.7
     */
    // @formatter:off
    public static final CSVFormat MONGODB_TSV = DEFAULT.builder()
            .setDelimiter(TAB)
            .setEscape(DOUBLE_QUOTE_CHAR)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setQuoteMode(QuoteMode.MINIMAL)
            .setSkipHeaderRecord(false)
            .build();
    // @formatter:off

    /**
     * Default MySQL format used by the {@code SELECT INTO OUTFILE} and {@code LOAD DATA INFILE} operations.
     *
     * <p>
     * This is a tab-delimited format with a LF character as the line separator. Values are not quoted and special
     * characters are escaped with {@code '\'}. The default NULL string is {@code "\\N"}.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter('\t')}</li>
     * <li>{@code setEscape('\\')}</li>
     * <li>{@code setIgnoreEmptyLines(false)}</li>
     * <li>{@code setQuote(null)}</li>
     * <li>{@code setRecordSeparator('\n')}</li>
     * <li>{@code setNullString("\\N")}</li>
     * <li>{@code setQuoteMode(QuoteMode.ALL_NON_NULL)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MySQL
     * @see <a href="http://dev.mysql.com/doc/refman/5.1/en/load-data.html"> http://dev.mysql.com/doc/refman/5.1/en/load
     *      -data.html</a>
     */
    // @formatter:off
    public static final CSVFormat MYSQL = DEFAULT.builder()
            .setDelimiter(TAB)
            .setEscape(BACKSLASH)
            .setIgnoreEmptyLines(false)
            .setQuote(null)
            .setRecordSeparator(LF)
            .setNullString("\\N")
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .build();
    // @formatter:off

    /**
     * Default Oracle format used by the SQL*Loader utility.
     *
     * <p>
     * This is a comma-delimited format with the system line separator character as the record separator.Values are
     * double quoted when needed and special characters are escaped with {@code '"'}. The default NULL string is
     * {@code ""}. Values are trimmed.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',') // default is {@code FIELDS TERMINATED BY ','}}</li>
     * <li>{@code setEscape('\\')}</li>
     * <li>{@code setIgnoreEmptyLines(false)}</li>
     * <li>{@code setQuote('"')  // default is {@code OPTIONALLY ENCLOSED BY '"'}}</li>
     * <li>{@code setNullString("\\N")}</li>
     * <li>{@code setTrim()}</li>
     * <li>{@code setSystemRecordSeparator()}</li>
     * <li>{@code setQuoteMode(QuoteMode.MINIMAL)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#Oracle
     * @see <a href="https://s.apache.org/CGXG">Oracle CSV Format Specification</a>
     * @since 1.6
     */
    // @formatter:off
    public static final CSVFormat ORACLE = DEFAULT.builder()
            .setDelimiter(COMMA)
            .setEscape(BACKSLASH)
            .setIgnoreEmptyLines(false)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setNullString("\\N")
            .setTrim(true)
            .setRecordSeparator(System.lineSeparator())
            .setQuoteMode(QuoteMode.MINIMAL)
            .build();
    // @formatter:off

    /**
     * Default PostgreSQL CSV format used by the {@code COPY} operation.
     *
     * <p>
     * This is a comma-delimited format with a LF character as the line separator. Values are double quoted and special
     * characters are escaped with {@code '"'}. The default NULL string is {@code ""}.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setEscape('"')}</li>
     * <li>{@code setIgnoreEmptyLines(false)}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setRecordSeparator('\n')}</li>
     * <li>{@code setNullString("")}</li>
     * <li>{@code setQuoteMode(QuoteMode.ALL_NON_NULL)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MySQL
     * @see <a href="https://www.postgresql.org/docs/current/static/sql-copy.html">PostgreSQL COPY command
     *          documentation</a>
     * @since 1.5
     */
    // @formatter:off
    public static final CSVFormat POSTGRESQL_CSV = DEFAULT.builder()
            .setDelimiter(COMMA)
            .setEscape(DOUBLE_QUOTE_CHAR)
            .setIgnoreEmptyLines(false)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .setNullString(EMPTY)
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .build();
    // @formatter:off

    /**
     * Default PostgreSQL text format used by the {@code COPY} operation.
     *
     * <p>
     * This is a tab-delimited format with a LF character as the line separator. Values are double quoted and special
     * characters are escaped with {@code '"'}. The default NULL string is {@code "\\N"}.
     * </p>
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter('\t')}</li>
     * <li>{@code setEscape('\\')}</li>
     * <li>{@code setIgnoreEmptyLines(false)}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setRecordSeparator('\n')}</li>
     * <li>{@code setNullString("\\N")}</li>
     * <li>{@code setQuoteMode(QuoteMode.ALL_NON_NULL)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#MySQL
     * @see <a href="https://www.postgresql.org/docs/current/static/sql-copy.html">PostgreSQL COPY command
     *          documentation</a>
     * @since 1.5
     */
    // @formatter:off
    public static final CSVFormat POSTGRESQL_TEXT = DEFAULT.builder()
            .setDelimiter(TAB)
            .setEscape(BACKSLASH)
            .setIgnoreEmptyLines(false)
            .setQuote(DOUBLE_QUOTE_CHAR)
            .setRecordSeparator(LF)
            .setNullString("\\N")
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .build();
    // @formatter:off

    /**
     * Comma separated format as defined by <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter(',')}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setRecordSeparator("\r\n")}</li>
     * <li>{@code setIgnoreEmptyLines(false)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#RFC4180
     */
    public static final CSVFormat RFC4180 = DEFAULT.builder().setIgnoreEmptyLines(false).build();

    private static final long serialVersionUID = 1L;

    /**
     * Tab-delimited format.
     *
     * <p>
     * The {@link CSVFormatBuilder} settings are:
     * </p>
     * <ul>
     * <li>{@code setDelimiter('\t')}</li>
     * <li>{@code setQuote('"')}</li>
     * <li>{@code setRecordSeparator("\r\n")}</li>
     * <li>{@code setIgnoreSurroundingSpaces(true)}</li>
     * </ul>
     *
     * @see CSVFormatPredefinedFormats#TDF
     */
    // @formatter:off
    public static final CSVFormat TDF = DEFAULT.builder()
            .setDelimiter(TAB)
            .setIgnoreSurroundingSpaces(true)
            .build();
    // @formatter:on

    public CSVFormat(final CSVFormatBuilder CSVFormatBuilder) {
        this.setDelimiter(CSVFormatBuilder.getDelimiter());
        this.setQuoteCharacter(CSVFormatBuilder.getQuoteCharacter());
        this.setQuoteMode(CSVFormatBuilder.getQuoteMode());
        this.setCommentMarker(CSVFormatBuilder.getCommentMarker());
        this.setEscapeCharacter(CSVFormatBuilder.getEscapeCharacter());
        this.setIgnoreSurroundingSpaces(CSVFormatBuilder.isIgnoreSurroundingSpaces());
        this.setAllowMissingColumnNames(CSVFormatBuilder.isAllowMissingColumnNames());
        this.setIgnoreEmptyLines(CSVFormatBuilder.isIgnoreEmptyLines());
        this.setRecordSeparator(CSVFormatBuilder.getRecordSeparator());
        this.setNullString(CSVFormatBuilder.getNullString());
        this.setHeaderComments(CSVFormatBuilder.getHeaderComments());
        this.setHeader(CSVFormatBuilder.getHeaders());
        this.setSkipHeaderRecord(CSVFormatBuilder.isSkipHeaderRecord());
        this.setIgnoreHeaderCase(CSVFormatBuilder.isIgnoreHeaderCase());
        this.setTrailingDelimiter(CSVFormatBuilder.isTrailingDelimiter());
        this.setTrim(CSVFormatBuilder.isTrim());
        this.setAutoFlush(CSVFormatBuilder.isAutoFlush());
        this.setQuotedNullString(CSVFormatBuilder.getQuotedNullString());
        this.setAllowDuplicateHeaderNames(CSVFormatBuilder.isAllowDuplicateHeaderNames());
        validate();
    }

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
    private CSVFormat(final String delimiter, final Character quoteChar, final QuoteMode quoteMode, final Character commentStart, final Character escape,
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

    private void append(final char c, final Appendable appendable) throws IOException {
        //try {
            appendable.append(c);
        //} catch (final IOException e) {
        //    throw new UncheckedIOException(e);
        //}
    }

    private void append(final CharSequence csq, final Appendable appendable) throws IOException {
        //try {
            appendable.append(csq);
        //} catch (final IOException e) {
        //    throw new UncheckedIOException(e);
        //}
    }

    /**
     * Creates a new Builder for this instance.
     *
     * @return a new Builder.
     */
    public CSVFormatBuilder builder() {
        return CSVFormatBuilder.create(this);
    }

    /**
     * Creates a copy of this instance.
     *
     * @return a copy of this instance.
     */
    public CSVFormat copy() {
        return builder().build();
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
     * Matches whether the next characters constitute a delimiter
     *
     * @param ch
     *            the current char
     * @param charSeq
     *            the match char sequence
     * @param startIndex
     *            where start to match
     * @param delimiter
     *            the delimiter
     * @param delimiterLength
     *            the delimiter length
     * @return true if the match is successful
     */
    private boolean isDelimiter(final char ch, final CharSequence charSeq, final int startIndex, final char[] delimiter, final int delimiterLength) {
        if (ch != delimiter[0]) {
            return false;
        }
        final int len = charSeq.length();
        if (startIndex + delimiterLength > len) {
            return false;
        }
        for (int i = 1; i < delimiterLength; i++) {
            if (charSeq.charAt(startIndex + i) != delimiter[i]) {
                return false;
            }
        }
        return true;
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

    /**
     * Parses the specified content.
     *
     * <p>
     * See also the various static parse methods on {@link CSVParser}.
     * </p>
     *
     * @param reader the input stream
     * @return a parser over a stream of {@link CSVRecord}s.
     * @throws IOException If an I/O error occurs
     */
    public CSVParser parse(final Reader reader) throws IOException {
        return new CSVParser(reader, this);
    }

    /**
     * Prints to the specified output.
     *
     * <p>
     * See also {@link CSVPrinter}.
     * </p>
     *
     * @param out the output.
     * @return a printer to an output.
     * @throws IOException thrown if the optional header cannot be printed.
     */
    public CSVPrinter print(final Appendable out) throws IOException {
        return new CSVPrinter(out, this);
    }

    /**
     * Prints to the specified output.
     *
     * <p>
     * See also {@link CSVPrinter}.
     * </p>
     *
     * @param out     the output.
     * @param charset A charset.
     * @return a printer to an output.
     * @throws IOException thrown if the optional header cannot be printed.
     * @since 1.5
     */
    @SuppressWarnings("resource")
    public CSVPrinter print(final File out, final Charset charset) throws IOException {
        // The writer will be closed when close() is called.
        return new CSVPrinter(new OutputStreamWriter(new FileOutputStream(out), charset), this);
    }

    /**
     * Prints the {@code value} as the next value on the line to {@code out}. The value will be escaped or encapsulated as needed. Useful when one wants to
     * avoid creating CSVPrinters. Trims the value if {@link #getTrim()} is true.
     *
     * @param value     value to output.
     * @param out       where to print the value.
     * @param newRecord if this a new record.
     * @throws IOException If an I/O error occurs.
     * @since 1.4
     */
    public void print(final Object value, final Appendable out, final boolean newRecord) throws IOException {
        // null values are considered empty
        // Only call CharSequence.toString() if you have to, helps GC-free use cases.
        CharSequence charSequence;
        if (value == null) {
            // https://issues.apache.org/jira/browse/CSV-203
            if (null == getNullString()) {
                charSequence = EMPTY;
            } else if (QuoteMode.ALL == getQuoteMode()) {
                charSequence = getQuotedNullString();
            } else {
                charSequence = getNullString();
            }
        } else if (value instanceof CharSequence) {
            charSequence = (CharSequence) value;
        } else if (value instanceof Reader) {
            print((Reader) value, out, newRecord);
            return;
        } else {
            charSequence = value.toString();
        }
        charSequence = getTrim() ? trim(charSequence) : charSequence;
        print(value, charSequence, out, newRecord);
    }

    private void print(final Object object, final CharSequence value, final Appendable out, final boolean newRecord) throws IOException {
        final int offset = 0;
        final int len = value.length();
        if (!newRecord) {
            out.append(getDelimiterString());
        }
        if (object == null) {
            out.append(value);
        } else if (isQuoteCharacterSet()) {
            // the original object is needed so can check for Number
            printWithQuotes(object, value, out, newRecord);
        } else if (isEscapeCharacterSet()) {
            printWithEscapes(value, out);
        } else {
            out.append(value, offset, len);
        }
    }

    /**
     * Prints to the specified output, returns a {@code CSVPrinter} which the caller MUST close.
     *
     * <p>
     * See also {@link CSVPrinter}.
     * </p>
     *
     * @param out     the output.
     * @param charset A charset.
     * @return a printer to an output.
     * @throws IOException thrown if the optional header cannot be printed.
     * @since 1.5
     */
    @SuppressWarnings("resource")
    public CSVPrinter print(final Path out, final Charset charset) throws IOException {
        return print(Files.newBufferedWriter(out, charset));
    }

    private void print(final Reader reader, final Appendable out, final boolean newRecord) throws IOException {
        // Reader is never null
        if (!newRecord) {
            append(getDelimiterString(), out);
        }
        if (isQuoteCharacterSet()) {
            printWithQuotes(reader, out);
        } else if (isEscapeCharacterSet()) {
            printWithEscapes(reader, out);
        } else if (out instanceof Writer) {
            IOUtils.copyLarge(reader, (Writer) out);
        } else {
            IOUtils.copy(reader, out);
        }

    }

    /**
     * Prints to the {@link System#out}.
     *
     * <p>
     * See also {@link CSVPrinter}.
     * </p>
     *
     * @return a printer to {@link System#out}.
     * @throws IOException thrown if the optional header cannot be printed.
     * @since 1.5
     */
    public CSVPrinter printer() throws IOException {
        return new CSVPrinter(System.out, this);
    }

    /**
     * Outputs the trailing delimiter (if set) followed by the record separator (if set).
     *
     * @param appendable where to write
     * @throws IOException If an I/O error occurs.
     * @since 1.4
     */
    public void println(final Appendable appendable) throws IOException {
        if (getTrailingDelimiter()) {
            append(getDelimiterString(), appendable);
        }
        if (getRecordSeparator() != null) {
            append(getRecordSeparator(), appendable);
        }
    }

    /**
     * Prints the given {@code values} to {@code out} as a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and new-line characters will be escaped. This method adds the record separator to the output after printing
     * the record, so there is no need to call {@link #println(Appendable)}.
     * </p>
     *
     * @param appendable    where to write.
     * @param values values to output.
     * @throws IOException If an I/O error occurs.
     * @since 1.4
     */
    public void printRecord(final Appendable appendable, final Object... values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            print(values[i], appendable, i == 0);
        }
        println(appendable);
    }

    /*
     * Note: Must only be called if escaping is enabled, otherwise will generate NPE.
     */
    private void printWithEscapes(final CharSequence charSeq, final Appendable appendable) throws IOException {
        int start = 0;
        int pos = 0;
        final int end = charSeq.length();

        final char[] delim = getDelimiterString().toCharArray();
        final int delimLength = delim.length;
        final char escape = getEscapeCharacter().charValue();

        while (pos < end) {
            char c = charSeq.charAt(pos);
            final boolean isDelimiterStart = isDelimiter(c, charSeq, pos, delim, delimLength);
            if (c == CR || c == LF || c == escape || isDelimiterStart) {
                // write out segment up until this char
                if (pos > start) {
                    appendable.append(charSeq, start, pos);
                }
                if (c == LF) {
                    c = 'n';
                } else if (c == CR) {
                    c = 'r';
                }

                appendable.append(escape);
                appendable.append(c);

                if (isDelimiterStart) {
                    for (int i = 1; i < delimLength; i++) {
                        pos++;
                        c = charSeq.charAt(pos);
                        appendable.append(escape);
                        appendable.append(c);
                    }
                }

                start = pos + 1; // start on the current char after this one
            }
            pos++;
        }

        // write last segment
        if (pos > start) {
            appendable.append(charSeq, start, pos);
        }
    }

    private void printWithEscapes(final Reader reader, final Appendable appendable) throws IOException {
        int start = 0;
        int pos = 0;

        @SuppressWarnings("resource") // Temp reader on input reader.
        final ExtendedBufferedReader bufferedReader = new ExtendedBufferedReader(reader);
        final char[] delim = getDelimiterString().toCharArray();
        final int delimLength = delim.length;
        final char escape = getEscapeCharacter().charValue();
        final StringBuilder builder = new StringBuilder(IOUtils.DEFAULT_BUFFER_SIZE);

        int c;
        while (-1 != (c = bufferedReader.read())) {
            builder.append((char) c);
            final boolean isDelimiterStart = isDelimiter((char) c, builder.toString() + new String(bufferedReader.lookAhead(delimLength - 1)), pos, delim,
                    delimLength);
            if (c == CR || c == LF || c == escape || isDelimiterStart) {
                // write out segment up until this char
                if (pos > start) {
                    append(builder.substring(start, pos), appendable);
                    builder.setLength(0);
                    pos = -1;
                }
                if (c == LF) {
                    c = 'n';
                } else if (c == CR) {
                    c = 'r';
                }

                append(escape, appendable);
                append((char) c, appendable);

                if (isDelimiterStart) {
                    for (int i = 1; i < delimLength; i++) {
                        c = bufferedReader.read();
                        append(escape, appendable);
                        append((char) c, appendable);
                    }
                }

                start = pos + 1; // start on the current char after this one
            }
            pos++;
        }

        // write last segment
        if (pos > start) {
            append(builder.substring(start, pos), appendable);
        }
    }

    /*
     * Note: must only be called if quoting is enabled, otherwise will generate NPE
     */
    // the original object is needed so can check for Number
    private void printWithQuotes(final Object object, final CharSequence charSeq, final Appendable out, final boolean newRecord) throws IOException {
        boolean quote = false;
        int start = 0;
        int pos = 0;
        final int len = charSeq.length();

        final char[] delim = getDelimiterString().toCharArray();
        final int delimLength = delim.length;
        final char quoteChar = getQuoteCharacter().charValue();
        // If escape char not specified, default to the quote char
        // This avoids having to keep checking whether there is an escape character
        // at the cost of checking against quote twice
        final char escapeChar = isEscapeCharacterSet() ? getEscapeCharacter().charValue() : quoteChar;

        QuoteMode quoteModePolicy = getQuoteMode();
        if (quoteModePolicy == null) {
            quoteModePolicy = QuoteMode.MINIMAL;
        }
        switch (quoteModePolicy) {
        case ALL:
        case ALL_NON_NULL:
            quote = true;
            break;
        case NON_NUMERIC:
            quote = !(object instanceof Number);
            break;
        case NONE:
            // Use the existing escaping code
            printWithEscapes(charSeq, out);
            return;
        case MINIMAL:
            if (len <= 0) {
                // always quote an empty token that is the first
                // on the line, as it may be the only thing on the
                // line. If it were not quoted in that case,
                // an empty line has no tokens.
                if (newRecord) {
                    quote = true;
                }
            } else {
                char c = charSeq.charAt(pos);

                if (c <= COMMENT) {
                    // Some other chars at the start of a value caused the parser to fail, so for now
                    // encapsulate if we start in anything less than '#'. We are being conservative
                    // by including the default comment char too.
                    quote = true;
                } else {
                    while (pos < len) {
                        c = charSeq.charAt(pos);
                        if (c == LF || c == CR || c == quoteChar || c == escapeChar || isDelimiter(c, charSeq, pos, delim, delimLength)) {
                            quote = true;
                            break;
                        }
                        pos++;
                    }

                    if (!quote) {
                        pos = len - 1;
                        c = charSeq.charAt(pos);
                        // Some other chars at the end caused the parser to fail, so for now
                        // encapsulate if we end in anything less than ' '
                        if (c <= SP) {
                            quote = true;
                        }
                    }
                }
            }

            if (!quote) {
                // no encapsulation needed - write out the original value
                out.append(charSeq, start, len);
                return;
            }
            break;
        default:
            throw new IllegalStateException("Unexpected Quote value: " + quoteModePolicy);
        }

        if (!quote) {
            // no encapsulation needed - write out the original value
            out.append(charSeq, start, len);
            return;
        }

        // we hit something that needed encapsulation
        out.append(quoteChar);

        // Pick up where we left off: pos should be positioned on the first character that caused
        // the need for encapsulation.
        while (pos < len) {
            final char c = charSeq.charAt(pos);
            if (c == quoteChar || c == escapeChar) {
                // write out the chunk up until this point
                out.append(charSeq, start, pos);
                out.append(escapeChar); // now output the escape
                start = pos; // and restart with the matched char
            }
            pos++;
        }

        // write the last segment
        out.append(charSeq, start, pos);
        out.append(quoteChar);
    }

    /**
     * Always use quotes unless QuoteMode is NONE, so we not have to look ahead.
     *
     * @param reader What to print
     * @param appendable Where to print it
     * @throws IOException If an I/O error occurs
     */
    private void printWithQuotes(final Reader reader, final Appendable appendable) throws IOException {

        if (getQuoteMode() == QuoteMode.NONE) {
            printWithEscapes(reader, appendable);
            return;
        }

        int pos = 0;

        final char quote = getQuoteCharacter().charValue();
        final StringBuilder builder = new StringBuilder(IOUtils.DEFAULT_BUFFER_SIZE);

        append(quote, appendable);

        int c;
        while (-1 != (c = reader.read())) {
            builder.append((char) c);
            if (c == quote) {
                // write out segment up until this char
                if (pos > 0) {
                    append(builder.substring(0, pos), appendable);
                    append(quote, appendable);
                    builder.setLength(0);
                    pos = -1;
                }

                append((char) c, appendable);
            }
            pos++;
        }

        // write last segment
        if (pos > 0) {
            append(builder.substring(0, pos), appendable);
        }

        append(quote, appendable);
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

        if (getQuoteCharacter() != null && CSVFormatHelper.contains(getDelimiterString(), getQuoteCharacter().charValue())) {
            throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + getQuoteCharacter() + "')");
        }

        if (getEscapeCharacter() != null && CSVFormatHelper.contains(getDelimiterString(), getEscapeCharacter().charValue())) {
            throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + getEscapeCharacter() + "')");
        }

        if (getCommentMarker() != null && CSVFormatHelper.contains(getDelimiterString(), getCommentMarker().charValue())) {
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
     * @see #DEFAULT
     * @see #RFC4180
     * @see #MYSQL
     * @see #EXCEL
     * @see #TDF
     */
    public static CSVFormat newFormat(final char delimiter) {
        return new CSVFormat(String.valueOf(delimiter), null, null, null, null, false, false, null, null, null, null, false, false, false, false, false, false,
                true);
    }

    static CharSequence trim(final CharSequence charSequence) {
        if (charSequence instanceof String) {
            return ((String) charSequence).trim();
        }
        final int count = charSequence.length();
        int len = count;
        int pos = 0;

        while (pos < len && charSequence.charAt(pos) <= SP) {
            pos++;
        }
        while (pos < len && charSequence.charAt(len - 1) <= SP) {
            len--;
        }
        return pos > 0 || len < count ? charSequence.subSequence(pos, len) : charSequence;
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

    /**
     * Returns a new {@code CSVFormat} that allows duplicate header names.
     *
     * @return a new {@code CSVFormat} that allows duplicate header names
     * @since 1.7
     * @deprecated Use {@link CSVFormatBuilder#setAllowDuplicateHeaderNames(boolean) Builder#setAllowDuplicateHeaderNames(true)}
     */
    @Deprecated
    public CSVFormat withAllowDuplicateHeaderNames() {
        return builder().setAllowDuplicateHeaderNames(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with duplicate header names behavior set to the given value.
     *
     * @param allowDuplicateHeaderNames the duplicate header names behavior, true to allow, false to disallow.
     * @return a new {@code CSVFormat} with duplicate header names behavior set to the given value.
     * @since 1.7
     * @deprecated Use {@link CSVFormatBuilder#setAllowDuplicateHeaderNames(boolean)}
     */
    @Deprecated
    public CSVFormat withAllowDuplicateHeaderNames(final boolean allowDuplicateHeaderNames) {
        return builder().setAllowDuplicateHeaderNames(allowDuplicateHeaderNames).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the missing column names behavior of the format set to {@code true}.
     *
     * @return A new CSVFormat that is equal to this but with the specified missing column names behavior.
     * @see CSVFormatBuilder#setAllowMissingColumnNames(boolean)
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setAllowMissingColumnNames(boolean) Builder#setAllowMissingColumnNames(true)}
     */
    @Deprecated
    public CSVFormat withAllowMissingColumnNames() {
        return builder().setAllowMissingColumnNames(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the missing column names behavior of the format set to the given value.
     *
     * @param allowMissingColumnNames the missing column names behavior, {@code true} to allow missing column names in the header line, {@code false} to cause
     *                                an {@link IllegalArgumentException} to be thrown.
     * @return A new CSVFormat that is equal to this but with the specified missing column names behavior.
     * @deprecated Use {@link CSVFormatBuilder#setAllowMissingColumnNames(boolean)}
     */
    @Deprecated
    public CSVFormat withAllowMissingColumnNames(final boolean allowMissingColumnNames) {
        return builder().setAllowMissingColumnNames(allowMissingColumnNames).build();
    }

    /**
     * Returns a new {@code CSVFormat} with whether to flush on close.
     *
     * @param autoFlush whether to flush on close.
     *
     * @return A new CSVFormat that is equal to this but with the specified autoFlush setting.
     * @since 1.6
     * @deprecated Use {@link CSVFormatBuilder#setAutoFlush(boolean)}
     */
    @Deprecated
    public CSVFormat withAutoFlush(final boolean autoFlush) {
        return builder().setAutoFlush(autoFlush).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the comment start marker of the format set to the specified character.
     *
     * Note that the comment start character is only recognized at the start of a line.
     *
     * @param commentMarker the comment start marker
     * @return A new CSVFormat that is equal to this one but with the specified character as the comment start marker
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setCommentMarker(char)}
     */
    @Deprecated
    public CSVFormat withCommentMarker(final char commentMarker) {
        return builder().setCommentMarker(commentMarker).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the comment start marker of the format set to the specified character.
     *
     * Note that the comment start character is only recognized at the start of a line.
     *
     * @param commentMarker the comment start marker, use {@code null} to disable
     * @return A new CSVFormat that is equal to this one but with the specified character as the comment start marker
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setCommentMarker(Character)}
     */
    @Deprecated
    public CSVFormat withCommentMarker(final Character commentMarker) {
        return builder().setCommentMarker(commentMarker).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the delimiter of the format set to the specified character.
     *
     * @param delimiter the delimiter character
     * @return A new CSVFormat that is equal to this with the specified character as delimiter
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setDelimiter(char)}
     */
    @Deprecated
    public CSVFormat withDelimiter(final char delimiter) {
        return builder().setDelimiter(delimiter).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the escape character of the format set to the specified character.
     *
     * @param escape the escape character
     * @return A new CSVFormat that is equal to this but with the specified character as the escape character
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setEscape(char)}
     */
    @Deprecated
    public CSVFormat withEscape(final char escape) {
        return builder().setEscape(escape).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the escape character of the format set to the specified character.
     *
     * @param escape the escape character, use {@code null} to disable
     * @return A new CSVFormat that is equal to this but with the specified character as the escape character
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setEscape(Character)}
     */
    @Deprecated
    public CSVFormat withEscape(final Character escape) {
        return builder().setEscape(escape).build();
    }

    /**
     * Returns a new {@code CSVFormat} using the first record as header.
     *
     * <p>
     * Calling this method is equivalent to calling:
     * </p>
     *
     * <pre>
     * CSVFormat format = aFormat.withHeader().withSkipHeaderRecord();
     * </pre>
     *
     * @return A new CSVFormat that is equal to this but using the first record as header.
     * @see CSVFormatBuilder#setSkipHeaderRecord(boolean)
     * @see CSVFormatBuilder#setHeader(String...)
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setHeader(String...) Builder#setHeader()}.{@link CSVFormatBuilder#setSkipHeaderRecord(boolean) setSkipHeaderRecord(true)}.
     */
    @Deprecated
    public CSVFormat withFirstRecordAsHeader() {
        // @formatter:off
        return builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        // @formatter:on
    }

    /**
     * Returns a new {@code CSVFormat} with the header of the format defined by the enum class.
     *
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * public enum Header {
     *     Name, Email, Phone
     * }
     *
     * CSVFormat format = aformat.withHeader(Header.class);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param headerEnum the enum defining the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return A new CSVFormat that is equal to this but with the specified header
     * @see CSVFormatBuilder#setHeader(String...)
     * @see CSVFormatBuilder#setSkipHeaderRecord(boolean)
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setHeader(Class)}
     */
    @Deprecated
    public CSVFormat withHeader(final Class<? extends Enum<?>> headerEnum) {
        return builder().setHeader(headerEnum).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the header of the format set from the result set metadata. The header can either be parsed automatically from the
     * input file with:
     *
     * <pre>
     * CSVFormat format = aformat.withHeader();
     * </pre>
     *
     * or specified manually with:
     *
     * <pre>
     * CSVFormat format = aformat.withHeader(resultSet);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param resultSet the resultSet for the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return A new CSVFormat that is equal to this but with the specified header
     * @throws SQLException SQLException if a database access error occurs or this method is called on a closed result set.
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setHeader(ResultSet)}
     */
    @Deprecated
    public CSVFormat withHeader(final ResultSet resultSet) throws SQLException {
        return builder().setHeader(resultSet).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the header of the format set from the result set metadata. The header can either be parsed automatically from the
     * input file with:
     *
     * <pre>
     * CSVFormat format = aformat.withHeader();
     * </pre>
     *
     * or specified manually with:
     *
     * <pre>
     * CSVFormat format = aformat.withHeader(metaData);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param resultSetMetaData the metaData for the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return A new CSVFormat that is equal to this but with the specified header
     * @throws SQLException SQLException if a database access error occurs or this method is called on a closed result set.
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setHeader(ResultSetMetaData)}
     */
    @Deprecated
    public CSVFormat withHeader(final ResultSetMetaData resultSetMetaData) throws SQLException {
        return builder().setHeader(resultSetMetaData).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the header of the format set to the given values. The header can either be parsed automatically from the input file
     * with:
     *
     * <pre>
     * CSVFormat format = aformat.withHeader();
     * </pre>
     *
     * or specified manually with:
     *
     * <pre>
     * CSVFormat format = aformat.withHeader(&quot;name&quot;, &quot;email&quot;, &quot;phone&quot;);
     * </pre>
     * <p>
     * The header is also used by the {@link CSVPrinter}.
     * </p>
     *
     * @param header the header, {@code null} if disabled, empty if parsed automatically, user specified otherwise.
     * @return A new CSVFormat that is equal to this but with the specified header
     * @see CSVFormatBuilder#setSkipHeaderRecord(boolean)
     * @deprecated Use {@link CSVFormatBuilder#setHeader(String...)}
     */
    @Deprecated
    public CSVFormat withHeader(final String... header) {
        return builder().setHeader(header).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the header comments of the format set to the given values. The comments will be printed first, before the headers.
     * This setting is ignored by the parser.
     *
     * <pre>
     * CSVFormat format = aformat.withHeaderComments(&quot;Generated by Apache Commons CSV.&quot;, Instant.now());
     * </pre>
     *
     * @param headerComments the headerComments which will be printed by the Printer before the actual CSV data.
     * @return A new CSVFormat that is equal to this but with the specified header
     * @see CSVFormatBuilder#setSkipHeaderRecord(boolean)
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setHeaderComments(Object...)}
     */
    @Deprecated
    public CSVFormat withHeaderComments(final Object... headerComments) {
        return builder().setHeaderComments(headerComments).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the empty line skipping behavior of the format set to {@code true}.
     *
     * @return A new CSVFormat that is equal to this but with the specified empty line skipping behavior.
     * @since {@link CSVFormatBuilder#setIgnoreEmptyLines(boolean)}
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setIgnoreEmptyLines(boolean) Builder#setIgnoreEmptyLines(true)}
     */
    @Deprecated
    public CSVFormat withIgnoreEmptyLines() {
        return builder().setIgnoreEmptyLines(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the empty line skipping behavior of the format set to the given value.
     *
     * @param ignoreEmptyLines the empty line skipping behavior, {@code true} to ignore the empty lines between the records, {@code false} to translate empty
     *                         lines to empty records.
     * @return A new CSVFormat that is equal to this but with the specified empty line skipping behavior.
     * @deprecated Use {@link CSVFormatBuilder#setIgnoreEmptyLines(boolean)}
     */
    @Deprecated
    public CSVFormat withIgnoreEmptyLines(final boolean ignoreEmptyLines) {
        return builder().setIgnoreEmptyLines(ignoreEmptyLines).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the header ignore case behavior set to {@code true}.
     *
     * @return A new CSVFormat that will ignore case header name.
     * @see CSVFormatBuilder#setIgnoreHeaderCase(boolean)
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setIgnoreHeaderCase(boolean) Builder#setIgnoreHeaderCase(true)}
     */
    @Deprecated
    public CSVFormat withIgnoreHeaderCase() {
        return builder().setIgnoreHeaderCase(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with whether header names should be accessed ignoring case.
     *
     * @param ignoreHeaderCase the case mapping behavior, {@code true} to access name/values, {@code false} to leave the mapping as is.
     * @return A new CSVFormat that will ignore case header name if specified as {@code true}
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setIgnoreHeaderCase(boolean)}
     */
    @Deprecated
    public CSVFormat withIgnoreHeaderCase(final boolean ignoreHeaderCase) {
        return builder().setIgnoreHeaderCase(ignoreHeaderCase).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the parser trimming behavior of the format set to {@code true}.
     *
     * @return A new CSVFormat that is equal to this but with the specified parser trimming behavior.
     * @see CSVFormatBuilder#setIgnoreSurroundingSpaces(boolean)
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setIgnoreSurroundingSpaces(boolean) Builder#setIgnoreSurroundingSpaces(true)}
     */
    @Deprecated
    public CSVFormat withIgnoreSurroundingSpaces() {
        return builder().setIgnoreSurroundingSpaces(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the parser trimming behavior of the format set to the given value.
     *
     * @param ignoreSurroundingSpaces the parser trimming behavior, {@code true} to remove the surrounding spaces, {@code false} to leave the spaces as is.
     * @return A new CSVFormat that is equal to this but with the specified trimming behavior.
     * @deprecated Use {@link CSVFormatBuilder#setIgnoreSurroundingSpaces(boolean)}
     */
    @Deprecated
    public CSVFormat withIgnoreSurroundingSpaces(final boolean ignoreSurroundingSpaces) {
        return builder().setIgnoreSurroundingSpaces(ignoreSurroundingSpaces).build();
    }

    /**
     * Returns a new {@code CSVFormat} with conversions to and from null for strings on input and output.
     * <ul>
     * <li><strong>Reading:</strong> Converts strings equal to the given {@code nullString} to {@code null} when reading records.</li>
     * <li><strong>Writing:</strong> Writes {@code null} as the given {@code nullString} when writing records.</li>
     * </ul>
     *
     * @param nullString the String to convert to and from {@code null}. No substitution occurs if {@code null}
     * @return A new CSVFormat that is equal to this but with the specified null conversion string.
     * @deprecated Use {@link CSVFormatBuilder#setNullString(String)}
     */
    @Deprecated
    public CSVFormat withNullString(final String nullString) {
        return builder().setNullString(nullString).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the quoteChar of the format set to the specified character.
     *
     * @param quoteChar the quote character
     * @return A new CSVFormat that is equal to this but with the specified character as quoteChar
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setQuote(char)}
     */
    @Deprecated
    public CSVFormat withQuote(final char quoteChar) {
        return builder().setQuote(quoteChar).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the quoteChar of the format set to the specified character.
     *
     * @param quoteChar the quote character, use {@code null} to disable.
     * @return A new CSVFormat that is equal to this but with the specified character as quoteChar
     * @throws IllegalArgumentException thrown if the specified character is a line break
     * @deprecated Use {@link CSVFormatBuilder#setQuote(Character)}
     */
    @Deprecated
    public CSVFormat withQuote(final Character quoteChar) {
        return builder().setQuote(quoteChar).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the output quote policy of the format set to the specified value.
     *
     * @param quoteMode the quote policy to use for output.
     *
     * @return A new CSVFormat that is equal to this but with the specified quote policy
     * @deprecated Use {@link CSVFormatBuilder#setQuoteMode(QuoteMode)}
     */
    @Deprecated
    public CSVFormat withQuoteMode(final QuoteMode quoteMode) {
        return builder().setQuoteMode(quoteMode).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the record separator of the format set to the specified character.
     *
     * <p>
     * <strong>Note:</strong> This setting is only used during printing and does not affect parsing. Parsing currently only works for inputs with '\n', '\r' and
     * "\r\n"
     * </p>
     *
     * @param recordSeparator the record separator to use for output.
     * @return A new CSVFormat that is equal to this but with the specified output record separator
     * @deprecated Use {@link CSVFormatBuilder#setRecordSeparator(char)}
     */
    @Deprecated
    public CSVFormat withRecordSeparator(final char recordSeparator) {
        return builder().setRecordSeparator(recordSeparator).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the record separator of the format set to the specified String.
     *
     * <p>
     * <strong>Note:</strong> This setting is only used during printing and does not affect parsing. Parsing currently only works for inputs with '\n', '\r' and
     * "\r\n"
     * </p>
     *
     * @param recordSeparator the record separator to use for output.
     * @return A new CSVFormat that is equal to this but with the specified output record separator
     * @throws IllegalArgumentException if recordSeparator is none of CR, LF or CRLF
     * @deprecated Use {@link CSVFormatBuilder#setRecordSeparator(String)}
     */
    @Deprecated
    public CSVFormat withRecordSeparator(final String recordSeparator) {
        return builder().setRecordSeparator(recordSeparator).build();
    }

    /**
     * Returns a new {@code CSVFormat} with skipping the header record set to {@code true}.
     *
     * @return A new CSVFormat that is equal to this but with the specified skipHeaderRecord setting.
     * @see CSVFormatBuilder#setSkipHeaderRecord(boolean)
     * @see CSVFormatBuilder#setHeader(String...)
     * @since 1.1
     * @deprecated Use {@link CSVFormatBuilder#setSkipHeaderRecord(boolean) Builder#setSkipHeaderRecord(true)}
     */
    @Deprecated
    public CSVFormat withSkipHeaderRecord() {
        return builder().setSkipHeaderRecord(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with whether to skip the header record.
     *
     * @param skipHeaderRecord whether to skip the header record.
     * @return A new CSVFormat that is equal to this but with the specified skipHeaderRecord setting.
     * @see CSVFormatBuilder#setHeader(String...)
     * @deprecated Use {@link CSVFormatBuilder#setSkipHeaderRecord(boolean)}
     */
    @Deprecated
    public CSVFormat withSkipHeaderRecord(final boolean skipHeaderRecord) {
        return builder().setSkipHeaderRecord(skipHeaderRecord).build();
    }

    /**
     * Returns a new {@code CSVFormat} with the record separator of the format set to the operating system's line separator string, typically CR+LF on Windows
     * and LF on Linux.
     *
     * <p>
     * <strong>Note:</strong> This setting is only used during printing and does not affect parsing. Parsing currently only works for inputs with '\n', '\r' and
     * "\r\n"
     * </p>
     *
     * @return A new CSVFormat that is equal to this but with the operating system's line separator string.
     * @since 1.6
     * @deprecated Use {@link CSVFormatBuilder#setRecordSeparator(String) setRecordSeparator(System.lineSeparator())}
     */
    @Deprecated
    public CSVFormat withSystemRecordSeparator() {
        return builder().setRecordSeparator(System.lineSeparator()).build();
    }

    /**
     * Returns a new {@code CSVFormat} to add a trailing delimiter.
     *
     * @return A new CSVFormat that is equal to this but with the trailing delimiter setting.
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setTrailingDelimiter(boolean) Builder#setTrailingDelimiter(true)}
     */
    @Deprecated
    public CSVFormat withTrailingDelimiter() {
        return builder().setTrailingDelimiter(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with whether to add a trailing delimiter.
     *
     * @param trailingDelimiter whether to add a trailing delimiter.
     * @return A new CSVFormat that is equal to this but with the specified trailing delimiter setting.
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setTrailingDelimiter(boolean)}
     */
    @Deprecated
    public CSVFormat withTrailingDelimiter(final boolean trailingDelimiter) {
        return builder().setTrailingDelimiter(trailingDelimiter).build();
    }

    /**
     * Returns a new {@code CSVFormat} to trim leading and trailing blanks. See {@link #getTrim()} for details of where this is used.
     *
     * @return A new CSVFormat that is equal to this but with the trim setting on.
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setTrim(boolean) Builder#setTrim(true)}
     */
    @Deprecated
    public CSVFormat withTrim() {
        return builder().setTrim(true).build();
    }

    /**
     * Returns a new {@code CSVFormat} with whether to trim leading and trailing blanks. See {@link #getTrim()} for details of where this is used.
     *
     * @param trim whether to trim leading and trailing blanks.
     * @return A new CSVFormat that is equal to this but with the specified trim setting.
     * @since 1.3
     * @deprecated Use {@link CSVFormatBuilder#setTrim(boolean)}
     */
    @Deprecated
    public CSVFormat withTrim(final boolean trim) {
        return builder().setTrim(trim).build();
    }
}
