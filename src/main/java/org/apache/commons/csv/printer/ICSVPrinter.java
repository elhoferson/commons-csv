package org.apache.commons.csv.printer;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ICSVPrinter extends Flushable, Closeable {
    void print(Object value) throws IOException;

    void printComment(String comment) throws IOException;

    void printHeaders(ResultSet resultSet) throws IOException, SQLException;

    void printRecord(Iterable<?> values) throws IOException;

    void printRecord(Object... values) throws IOException;

    void printRecords(Iterable<?> values) throws IOException;

    void printRecords(Object... values) throws IOException;

    void printRecords(ResultSet resultSet) throws SQLException, IOException;

    void printRecords(ResultSet resultSet, boolean printHeader) throws SQLException, IOException;

    void print(Object value, boolean newRecord) throws IOException;

    void println() throws IOException;
}
