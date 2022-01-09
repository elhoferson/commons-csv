package org.apache.commons.csv.parser;

public enum Type {
    /** Token has no valid content, i.e. is in its initialized state. */
    INVALID,

    /** Token with content, at beginning or in the middle of a line. */
    TOKEN,

    /** Token (which can have content) when the end of file is reached. */
    EOF,

    /** Token with content when the end of a line is reached. */
    EORECORD,

    /** Token is a comment line. */
    COMMENT
}
