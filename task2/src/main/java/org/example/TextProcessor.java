package org.example;

import java.io.*;
import java.util.*;

public class TextProcessor {

    public static MaxWordsLine findLineWithMaxWords(List<String> lines) {
        int maxWordCount = -1;
        String bestLineContent = null;
        int bestLineNumber = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] words = line.trim().isEmpty() ? new String[0] : line.trim().split("\\s+");

            int count = words.length;
            if (count > maxWordCount) {
                maxWordCount = count;
                bestLineContent = line;
                bestLineNumber = i + 1;
            }
        }

        return new MaxWordsLine(
                bestLineNumber,
                Objects.requireNonNullElse(bestLineContent, ""),
                Math.max(maxWordCount, 0)
        );
    }
}
