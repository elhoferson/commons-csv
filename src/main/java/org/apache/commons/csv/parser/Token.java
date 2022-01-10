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

package org.apache.commons.csv.parser;

import static org.apache.commons.csv.parser.Type.INVALID;

/**
 * Internal token representation.
 * <p/>
 * It is used as contract between the lexer and the parser.
 */
public class Token {

    /** length of the initial token (content-)buffer */
    private static final int INITIAL_TOKEN_LENGTH = 50;

    /** Token type */
    public Type type = INVALID;

    /** The content buffer. */
    public final StringBuilder content = new StringBuilder(INITIAL_TOKEN_LENGTH);

    /** Token ready flag: indicates a valid token with content (ready for the parser). */
    public boolean isReady;

    public boolean isQuoted;

    public void reset() {
        content.setLength(0);
        type = INVALID;
        isReady = false;
        isQuoted = false;
    }

    /**
     * Eases IDE debugging.
     *
     * @return a string helpful for debugging.
     */
    @Override
    public String toString() {
        return type.name() + " [" + content.toString() + "]";
    }
}
