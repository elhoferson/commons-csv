package org.apache.commons.csv.record;

import org.apache.commons.csv.parser.BaseCSVParser;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CSVRecordIterator implements Iterator<CSVRecord> {
    private final BaseCSVParser csvParser;
    private CSVRecord current;

    public CSVRecordIterator(BaseCSVParser csvParser) {
        this.csvParser = csvParser;
    }

    private CSVRecord getNextRecord() {
        try {
            return csvParser.nextRecord();
        } catch (final IOException e) {
            throw new IllegalStateException(
                    e.getClass().getSimpleName() + " reading next record: " + e.toString(), e);
        }
    }

    @Override
    public boolean hasNext() {
        if (csvParser.isClosed()) {
            return false;
        }
        if (this.current == null) {
            this.current = this.getNextRecord();
        }

        return this.current != null;
    }

    @Override
    public CSVRecord next() {
        if (csvParser.isClosed()) {
            throw new NoSuchElementException("CSVParser has been closed");
        }
        CSVRecord next = this.current;
        this.current = null;

        if (next == null) {
            // hasNext() wasn't called before
            next = this.getNextRecord();
            if (next == null) {
                throw new NoSuchElementException("No more CSV records available");
            }
        }

        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
