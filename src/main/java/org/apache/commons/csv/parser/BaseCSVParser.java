package org.apache.commons.csv.parser;

import org.apache.commons.csv.Constants;
import org.apache.commons.csv.format.ICSVFormat;
import org.apache.commons.csv.format.QuoteMode;
import org.apache.commons.csv.record.CSVRecord;
import org.apache.commons.csv.record.CSVRecordIterator;
import org.apache.commons.csv.record.Headers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.apache.commons.csv.parser.Type.TOKEN;

public class BaseCSVParser implements ICSVParser {
    protected final ICSVFormat format;
    protected final Headers headers;
    protected final CSVRecordIterator csvRecordIterator;
    /**
     * Lexer offset when the parser does not start parsing at the beginning of the source. Usually used in combination
     * with {@link #recordNumber}.
     */
    protected final long characterOffset;
    /**
     * A record buffer for getRecord(). Grows as necessary and is reused.
     */
    private final List<String> recordList = new ArrayList<>();
    private final Token reusableToken = new Token();
    /**
     * The next record number to assign.
     */
    protected long recordNumber;
    private final ILexer lexer;

    public BaseCSVParser(ICSVFormat format, final long recordNumber, final long characterOffset, ILexer lexer) throws IOException {
        this.format = format.copy();
        this.lexer = lexer;
        this.headers = createHeaders();
        this.csvRecordIterator = new CSVRecordIterator(this);
        this.recordNumber = recordNumber - 1;
        this.characterOffset = characterOffset;
    }

    private void addRecordValue(final boolean lastRecord) {
        final String input = this.reusableToken.content.toString();
        final String inputClean = this.format.getTrim() ? input.trim() : input;
        if (lastRecord && inputClean.isEmpty() && this.format.getTrailingDelimiter()) {
            return;
        }
        this.recordList.add(handleNull(inputClean));
    }

    /**
     * Closes resources.
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (this.lexer != null) {
            this.lexer.close();
        }
    }

    private Map<String, Integer> createEmptyHeaderMap() {
        return this.format.getIgnoreHeaderCase() ?
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER) :
                new LinkedHashMap<>();
    }

    /**
     * Creates the name to index mapping if the format defines a header.
     *
     * @return null if the format has no header.
     * @throws IOException if there is a problem reading the header or skipping the first record
     */
    protected Headers createHeaders() throws IOException {
        Map<String, Integer> hdrMap = null;
        List<String> headerNames = null;
        final String[] formatHeader = this.format.getHeader();
        if (formatHeader != null) {
            hdrMap = createEmptyHeaderMap();
            String[] headerRecord = null;
            if (formatHeader.length == 0) {
                // read the header from the first line of the file
                final CSVRecord nextRecord = this.nextRecord();
                if (nextRecord != null) {
                    headerRecord = nextRecord.values();
                }
            } else {
                if (this.format.getSkipHeaderRecord()) {
                    this.nextRecord();
                }
                headerRecord = formatHeader;
            }

            // build the name to index mappings
            if (headerRecord != null) {
                for (int i = 0; i < headerRecord.length; i++) {
                    final String header = headerRecord[i];
                    final boolean emptyHeader = header == null || header.trim().isEmpty();
                    if (emptyHeader && !this.format.getAllowMissingColumnNames()) {
                        throw new IllegalArgumentException(
                                "A header name is missing in " + Arrays.toString(headerRecord));
                    }
                    // Note: This will always allow a duplicate header if the header is empty
                    final boolean containsHeader = header != null && hdrMap.containsKey(header);
                    if (containsHeader && !emptyHeader && !this.format.getAllowDuplicateHeaderNames()) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "The header contains a duplicate name: \"%s\" in %s. If this is valid then use ICSVFormat.withAllowDuplicateHeaderNames().",
                                        header, Arrays.toString(headerRecord)));
                    }
                    if (header != null) {
                        hdrMap.put(header, i);
                        if (headerNames == null) {
                            headerNames = new ArrayList<>(headerRecord.length);
                        }
                        headerNames.add(header);
                    }
                }
            }
        }
        if (headerNames == null) {
            headerNames = Collections.emptyList(); //immutable
        } else {
            headerNames = Collections.unmodifiableList(headerNames);
        }
        return new Headers(hdrMap, headerNames);
    }

    /**
     * Returns the current line number in the input stream.
     *
     * <p>
     * <strong>ATTENTION:</strong> If your CSV input has multi-line values, the returned number does not correspond to
     * the record number.
     * </p>
     *
     * @return current line number
     */
    @Override
    public long getCurrentLineNumber() {
        return this.lexer.getCurrentLineNumber();
    }

    /**
     * Gets the first end-of-line string encountered.
     *
     * @return the first end-of-line string
     * @since 1.5
     */
    public String getFirstEndOfLine() {
        return lexer.getFirstEol();
    }

    /**
     * Returns a copy of the header map.
     * <p>
     * The map keys are column names. The map values are 0-based indices.
     * </p>
     * <p>
     * Note: The map can only provide a one-to-one mapping when the format did not
     * contain null or duplicate column names.
     * </p>
     *
     * @return a copy of the header map.
     */
    @Override
    public Map<String, Integer> getHeaderMap() {
        if (this.headers.getHeaderMap() == null) {
            return null;
        }
        final Map<String, Integer> map = createEmptyHeaderMap();
        map.putAll(this.headers.getHeaderMap());
        return map;
    }

    /**
     * Returns the header map.
     *
     * @return the header map.
     */
    @Override
    public Map<String, Integer> getHeaderMapRaw() {
        return this.headers.getHeaderMap();
    }

    /**
     * Returns a read-only list of header names that iterates in column order.
     * <p>
     * Note: The list provides strings that can be used as keys in the header map.
     * The list will not contain null column names if they were present in the input
     * format.
     * </p>
     *
     * @return read-only list of header names that iterates in column order.
     * @see #getHeaderMap()
     * @since 1.7
     */
    @Override
    public List<String> getHeaderNames() {
        return Collections.unmodifiableList(headers.getHeaderNames());
    }

    /**
     * Returns the current record number in the input stream.
     *
     * <p>
     * <strong>ATTENTION:</strong> If your CSV input has multi-line values, the returned number does not correspond to
     * the line number.
     * </p>
     *
     * @return current record number
     */
    @Override
    public long getRecordNumber() {
        return this.recordNumber;
    }

    /**
     * Parses the CSV input according to the given format and returns the content as a list of
     * {@link CSVRecord CSVRecords}.
     *
     * <p>
     * The returned content starts at the current parse-position in the stream.
     * </p>
     *
     * @return list of {@link CSVRecord CSVRecords}, may be empty
     * @throws IOException on parse error or input read-failure
     */
    @Override
    public List<CSVRecord> getRecords() throws IOException {
        CSVRecord rec;
        final List<CSVRecord> records = new ArrayList<>();
        while ((rec = this.nextRecord()) != null) {
            records.add(rec);
        }
        return records;
    }

    /**
     * Handle whether input is parsed as null
     *
     * @param input the cell data to further processed
     * @return null if input is parsed as null, or input itself if input isn't parsed as null
     */
    private String handleNull(final String input) {
        final boolean isQuoted = this.reusableToken.isQuoted;
        final String nullString = format.getNullString();
        final boolean strictQuoteMode = isStrictQuoteMode();
        if (input.equals(nullString)) {
            // nullString = NULL(String), distinguish between "NULL" and NULL in ALL_NON_NULL or NON_NUMERIC quote mode
            return strictQuoteMode && isQuoted ? input : null;
        }
        // don't set nullString, distinguish between "" and ,, (absent values) in All_NON_NULL or NON_NUMERIC quote mode
        return strictQuoteMode && nullString == null && input.isEmpty() && !isQuoted ? null : input;
    }

    /**
     * Tests whether this parser is closed.
     *
     * @return whether this parser is closed.
     */
    public boolean isClosed() {
        return this.lexer.isClosed();
    }

    /**
     * Tests whether the format's {@link QuoteMode} is {@link QuoteMode#ALL_NON_NULL} or {@link QuoteMode#NON_NUMERIC}.
     *
     * @return true if the format's {@link QuoteMode} is {@link QuoteMode#ALL_NON_NULL} or
     * {@link QuoteMode#NON_NUMERIC}.
     */
    private boolean isStrictQuoteMode() {
        return this.format.getQuoteMode() == QuoteMode.ALL_NON_NULL ||
                this.format.getQuoteMode() == QuoteMode.NON_NUMERIC;
    }

    /**
     * Returns the record iterator.
     *
     * <p>
     * An {@link IOException} caught during the iteration are re-thrown as an
     * {@link IllegalStateException}.
     * </p>
     * <p>
     * If the parser is closed a call to {@link Iterator#next()} will throw a
     * {@link NoSuchElementException}.
     * </p>
     */
    @Override
    public Iterator<CSVRecord> iterator() {
        return csvRecordIterator;
    }

    /**
     * Parses the next record from the current point in the stream.
     *
     * @return the record as an array of values, or {@code null} if the end of the stream has been reached
     * @throws IOException on parse error or input read-failure
     */
    @Override
    public CSVRecord nextRecord() throws IOException {
        CSVRecord result = null;
        this.recordList.clear();
        StringBuilder sb = null;
        final long startCharPosition = lexer.getCharacterPosition() + this.characterOffset;
        do {
            this.reusableToken.reset();
            this.lexer.nextToken(this.reusableToken);
            switch (this.reusableToken.type) {
                case TOKEN:
                    this.addRecordValue(false);
                    break;
                case EORECORD:
                    this.addRecordValue(true);
                    break;
                case EOF:
                    if (this.reusableToken.isReady) {
                        this.addRecordValue(true);
                    }
                    break;
                case INVALID:
                    throw new IOException("(line " + this.getCurrentLineNumber() + ") invalid parse sequence");
                case COMMENT: // Ignored currently
                    if (sb == null) { // first comment for this record
                        sb = new StringBuilder();
                    } else {
                        sb.append(Constants.LF);
                    }
                    sb.append(this.reusableToken.content);
                    this.reusableToken.type = TOKEN; // Read another token
                    break;
                default:
                    throw new IllegalStateException("Unexpected Token type: " + this.reusableToken.type);
            }
        } while (this.reusableToken.type == TOKEN);

        if (!this.recordList.isEmpty()) {
            this.recordNumber++;
            final String comment = sb == null ? null : sb.toString();
            result = new CSVRecord(this, this.recordList.toArray(Constants.EMPTY_STRING_ARRAY), comment,
                    this.recordNumber, startCharPosition);
        }
        return result;
    }

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     *
     * @return a sequential {@code Stream} with this collection as its source.
     * @since 1.9.0
     */
    @Override
    public Stream<CSVRecord> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }
}
