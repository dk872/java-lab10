package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IOTest {

    @Test
    void testFindLineWithMaxWordsBasic() {
        List<String> lines = List.of(
                "Hello world",
                "This is a line with seven words",
                "Short"
        );
        MaxWordsLine res = TextProcessor.findLineWithMaxWords(lines);

        assertEquals(2, res.lineNumber);
        assertEquals(7, res.wordCount);
    }

    @Test
    void testFindLineWithMaxWordsEmptyFile() {
        List<String> lines = List.of();
        MaxWordsLine res = TextProcessor.findLineWithMaxWords(lines);

        assertEquals(0, res.wordCount);
        assertEquals("", res.line);
    }

    @Test
    void testSerializeDeserializeObject() throws Exception {
        File tmp = File.createTempFile("ser", ".bin");
        tmp.deleteOnExit();
        MaxWordsLine obj = new MaxWordsLine(1, "abc def", 2);
        FileManager.serializeObject(tmp, obj);

        Object restored = FileManager.deserializeObject(tmp);
        assertInstanceOf(MaxWordsLine.class, restored);
        MaxWordsLine r = (MaxWordsLine) restored;

        assertEquals(2, r.wordCount);
        assertEquals("abc def", r.line);
    }

    @Test
    void testEncryptDecryptStreamRoundTrip() throws Exception {
        File in = File.createTempFile("int", ".txt");
        File enc = File.createTempFile("enc", ".bin");
        File dec = File.createTempFile("dec", ".txt");
        in.deleteOnExit(); enc.deleteOnExit(); dec.deleteOnExit();

        try (Writer w = new OutputStreamWriter(new FileOutputStream(in), StandardCharsets.UTF_8)) {
            w.write("hello world");
        }

        FileManager.encryptFile(in, enc, "K");
        FileManager.decryptFile(enc, dec, "K");

        String content;
        try (BufferedReader r = new BufferedReader(new FileReader(dec, StandardCharsets.UTF_8))) {
            content = r.readLine();
        }
        assertEquals("hello world", content);
    }

    @Test
    void testEncryptDecryptStreamWrongKey() throws Exception {
        File in = File.createTempFile("in2", ".txt");
        File enc = File.createTempFile("enc2", ".bin");
        File dec = File.createTempFile("dec2", ".txt");
        in.deleteOnExit(); enc.deleteOnExit(); dec.deleteOnExit();

        try (Writer w = new OutputStreamWriter(new FileOutputStream(in), StandardCharsets.UTF_8)) {
            w.write("test data");
        }

        FileManager.encryptFile(in, enc, "X");
        FileManager.decryptFile(enc, dec, "Y");

        String content;
        try (BufferedReader r = new BufferedReader(new FileReader(dec, StandardCharsets.UTF_8))) {
            content = r.readLine();
        }

        assertNotEquals("test data", content);
    }

    @Test
    void testCryptoFilterWriterReaderRoundTrip() throws Exception {
        File tmp = File.createTempFile("crypto", ".txt");
        tmp.deleteOnExit();

        FileManager.writeEncryptedText(tmp, "Secret Message", "KEY");
        String back = FileManager.readEncryptedText(tmp, "KEY");

        assertEquals("Secret Message", back);
    }

    @Test
    void testCryptoFilterWriterReaderEmptyText() throws Exception {
        File tmp = File.createTempFile("crypto2", ".txt");
        tmp.deleteOnExit();

        FileManager.writeEncryptedText(tmp, "", "A");
        String back = FileManager.readEncryptedText(tmp, "A");

        assertEquals("", back);
    }

    @Test
    void testTagCounterSortedByName() {
        Map<String, Integer> freq = new HashMap<>();
        freq.put("div", 3);
        freq.put("a", 5);
        freq.put("body", 1);

        List<Map.Entry<String, Integer>> sorted = TagCounter.sortedByName(freq);
        List<String> tags = sorted.stream().map(Map.Entry::getKey).toList();

        assertEquals(List.of("a", "body", "div"), tags);
    }

    @Test
    void testTagCounterSortedByFrequency() {
        Map<String, Integer> freq = new HashMap<>();
        freq.put("x", 5);
        freq.put("y", 2);
        freq.put("z", 2);
        List<Map.Entry<String, Integer>> sorted = TagCounter.sortedByFrequency(freq);

        assertEquals("y", sorted.get(0).getKey());
        assertEquals("z", sorted.get(1).getKey());
    }

    @Test
    void testFindLineWithMaxWordsTies() {
        List<String> lines = List.of(
                "one two three",
                "four five six",
                "seven eight"
        );
        MaxWordsLine res = TextProcessor.findLineWithMaxWords(lines);

        assertEquals(1, res.lineNumber);
        assertEquals(3, res.wordCount);
        assertEquals("one two three", res.line);
    }

    @Test
    void testFindLineWithMaxWordsSpacesAndSpecialChars() {
        List<String> lines = List.of(
                "    ",
                "\t\t",
                " !@#$ % ^&*()  ",
                "word1 word2"
        );
        MaxWordsLine res = TextProcessor.findLineWithMaxWords(lines);

        assertEquals(3, res.lineNumber);
        assertEquals(3, res.wordCount);
        assertEquals(" !@#$ % ^&*()  ", res.line);
    }

    @Test
    void testEncryptFileEmptyKey() throws IOException {
        File input = File.createTempFile("test", ".txt");
        File output = File.createTempFile("out", ".bin");
        input.deleteOnExit();
        output.deleteOnExit();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FileManager.encryptFile(input, output, "");
        });

        assertEquals("Key must be non-empty", exception.getMessage());
    }

    @Test
    void testEncryptFileNullKey() throws IOException {
        File input = File.createTempFile("test", ".txt");
        File output = File.createTempFile("out", ".bin");
        input.deleteOnExit();
        output.deleteOnExit();

        assertThrows(NullPointerException.class, () -> {
            FileManager.encryptFile(input, output, null);
        });
    }

    @Test
    void testWriteEncryptedTextEmptyKey() throws IOException {
        File output = File.createTempFile("crypto", ".txt");
        output.deleteOnExit();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FileManager.writeEncryptedText(output, "some text", "");
        });

        assertEquals("Key must be non-empty", exception.getMessage());
    }

    @Test
    void testWriteEncryptedTextNullKey() throws IOException {
        File output = File.createTempFile("crypto", ".txt");
        output.deleteOnExit();

        assertThrows(IllegalArgumentException.class, () -> {
            FileManager.writeEncryptedText(output, "some text", null);
        });
    }
}
