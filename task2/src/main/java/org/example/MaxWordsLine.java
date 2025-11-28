package org.example;

import java.io.Serializable;

public class MaxWordsLine implements Serializable {
    public final int lineNumber;
    public final String line;
    public final int wordCount;

    public MaxWordsLine(int lineNumber, String line, int wordCount) {
        this.lineNumber = lineNumber;
        this.line = line;
        this.wordCount = wordCount;
    }

    @Override
    public String toString() {
        return "Line #" + lineNumber + " (words=" + wordCount + "): " + line;
    }
}