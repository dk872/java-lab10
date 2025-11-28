package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;

public class FileManager {
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        try {
            FileHandler fileHandler = new FileHandler("filemanager.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            LOGGER.addHandler(consoleHandler);

            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("FileManager logger init failed: " + e.getMessage());
        }
    }

    public static List<String> readLines(File file) throws IOException {
        LOGGER.fine("Attempting to read file: " + file.getName());
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lines.add(currentLine);
            }
        }
        LOGGER.info("Read " + lines.size() + " lines from " + file.getAbsolutePath());
        return lines;
    }

    public static void serializeObject(File outFile, Serializable obj) throws IOException {
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(outFile))) {
            objectOut.writeObject(obj);
            LOGGER.info("Serialized object to " + outFile.getAbsolutePath());
        }
    }

    public static Object deserializeObject(File inFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(inFile))) {
            Object obj = objectIn.readObject();
            LOGGER.info("Deserialized object from " + inFile.getAbsolutePath());
            return obj;
        }
    }

    public static void encryptFile(File inputFile, File outputFile, String key) throws IOException {
        try (BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(inputFile));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            CipherUtils.encryptStream(bufferedIn, bufferedOut, key);
            LOGGER.info("Encrypted file " + inputFile.getAbsolutePath() + " -> " + outputFile.getAbsolutePath());
        }
    }

    public static void decryptFile(File inputFile, File outputFile, String key) throws IOException {
        try (BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(inputFile));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            CipherUtils.decryptStream(bufferedIn, bufferedOut, key);
            LOGGER.info("Decrypted file " + inputFile.getAbsolutePath() + " -> " + outputFile.getAbsolutePath());
        }
    }

    public static void writeEncryptedText(File outputFile, String text, String key) throws IOException {
        try (CryptoFilterWriter filterWriter = new CryptoFilterWriter(new FileWriter(outputFile, StandardCharsets.UTF_8), key)) {
            filterWriter.write(text);
            LOGGER.info("Encrypted text written to " + outputFile.getAbsolutePath());
        }
    }

    public static String readEncryptedText(File inputFile, String key) throws IOException {
        try (CryptoFilterReader filterReader = new CryptoFilterReader(new FileReader(inputFile, StandardCharsets.UTF_8), key)) {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[2048];
            int readCount;
            while ((readCount = filterReader.read(buffer)) != -1) builder.append(buffer, 0, readCount);
            LOGGER.info("Encrypted text read and decrypted from " + inputFile.getAbsolutePath());
            return builder.toString();
        }
    }
}
