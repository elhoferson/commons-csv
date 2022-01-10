package org.apache.commons.csv.parser;

import java.io.Closeable;
import java.io.IOException;

public interface ILexer extends Closeable {
    long getCharacterPosition();

    long getCurrentLineNumber();

    String getFirstEol();

    boolean isClosed();

    Token nextToken(Token token) throws IOException;

    int readEscape() throws IOException;

    void trimTrailingSpaces(final StringBuilder buffer);
}
