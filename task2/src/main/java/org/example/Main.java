package org.example;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static ResourceBundle messages;
    private static Locale currentLocale = Locale.of("en");

    static {
        try {
            LOGGER.setLevel(Level.INFO);

            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);
            LOGGER.addHandler(consoleHandler);

            LOGGER.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println("Logger initialization failed: " + e.getMessage());
        }

        loadMessages();
    }

    private static void loadMessages() {
        try {
            messages = ResourceBundle.getBundle("location.messages", currentLocale);
            LOGGER.info("Loaded resource bundle for locale: " + currentLocale);
        } catch (MissingResourceException e) {
            LOGGER.severe("Failed to load resource bundle: " + e.getMessage());
            System.err.println("Resource bundle not found. Using default messages.");
        }
    }

    private static String getMessage(String key) {
        try {
            return messages.getString(key);
        } catch (MissingResourceException | NullPointerException e) {
            return key;
        }
    }

    public static void main(String[] args) {
        LOGGER.info("Program started");
        label:
        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();
            try {
                switch (command) {
                    case "1":
                        doMaxWordsInFile();
                        break;
                    case "2":
                        doEncryptFile();
                        break;
                    case "3":
                        doDecryptFile();
                        break;
                    case "4":
                        doTagCount();
                        break;
                    case "5":
                        doFilterWriterDemo();
                        break;
                    case "6":
                        doDeserializeObject();
                        break;
                    case "7":
                        changeLanguage();
                        break;
                    case "0":
                        LOGGER.info("Exiting program");
                        System.out.println(getMessage("menu.exit.message"));
                        break label;
                    default:
                        LOGGER.warning("Unknown option: " + command);
                        System.out.println(getMessage("menu.unknown"));
                        break;
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error occurred: " + ex.getMessage(), ex);
                System.out.println(getMessage("error.occurred") + ": " + ex.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n" + getMessage("menu.title"));
        System.out.println("1) " + getMessage("menu.option1"));
        System.out.println("2) " + getMessage("menu.option2"));
        System.out.println("3) " + getMessage("menu.option3"));
        System.out.println("4) " + getMessage("menu.option4"));
        System.out.println("5) " + getMessage("menu.option5"));
        System.out.println("6) " + getMessage("menu.option6"));
        System.out.println("7) " + getMessage("menu.option7"));
        System.out.println("0) " + getMessage("menu.exit"));
        System.out.print("> ");
    }

    private static void changeLanguage() {
        System.out.println("\n" + getMessage("language.select"));
        System.out.println("1) English");
        System.out.println("2) Українська");
        System.out.println("3) Español");
        System.out.print("> ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                currentLocale = Locale.of("en");
                break;
            case "2":
                currentLocale = Locale.of("uk");
                break;
            case "3":
                currentLocale = Locale.of("es");
                break;
            default:
                System.out.println(getMessage("language.invalid"));
                LOGGER.warning("Invalid language choice: " + choice);
                return;
        }

        loadMessages();
        System.out.println(getMessage("language.changed"));
        LOGGER.info("Language changed to: " + currentLocale);
    }

    private static void doMaxWordsInFile() throws Exception {
        System.out.print(getMessage("prompt.input.file") + ": ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            LOGGER.severe("File not found: " + inputFile.getAbsolutePath());
            throw new IllegalArgumentException(getMessage("error.file.notfound") + ": " + inputFile.getAbsolutePath());
        }

        List<String> fileLines = FileManager.readLines(inputFile);
        MaxWordsLine result = TextProcessor.findLineWithMaxWords(fileLines);
        System.out.println(getMessage("result") + ": " + result);
        LOGGER.info("Found line with max words: " + result);

        System.out.print(getMessage("prompt.output.serialize") + ": ");
        String serializationPath = scanner.nextLine().trim();
        File serializationFile = new File(serializationPath);
        FileManager.serializeObject(serializationFile, result);
        System.out.println(getMessage("success.serialized") + ": " + serializationFile.getAbsolutePath());
        LOGGER.info("Serialized result to: " + serializationFile.getAbsolutePath());
        LOGGER.config("Completed searching for string with maximum number of words");
    }

    private static void doEncryptFile() throws Exception {
        System.out.print(getMessage("prompt.encrypt.input") + ": ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            LOGGER.severe("File not found: " + inputFile.getAbsolutePath());
            throw new FileNotFoundException(getMessage("error.file.notfound") + ": " + inputFile.getAbsolutePath());
        }

        System.out.print(getMessage("prompt.output.file") + ": ");
        File outputFile = new File(scanner.nextLine().trim());

        System.out.print(getMessage("prompt.key") + ": ");
        String keyString = scanner.nextLine();
        if (keyString == null || keyString.isEmpty()) {
            LOGGER.severe("Empty key provided");
            throw new IllegalArgumentException(getMessage("error.key.empty"));
        }

        FileManager.encryptFile(inputFile, outputFile, keyString);
        System.out.println(getMessage("success.encrypted") + ": " + outputFile.getAbsolutePath());
        LOGGER.info("File encrypted: " + outputFile.getAbsolutePath());
    }

    private static void doDecryptFile() throws Exception {
        System.out.print(getMessage("prompt.decrypt.input") + ": ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            LOGGER.severe("File not found: " + inputFile.getAbsolutePath());
            throw new FileNotFoundException(getMessage("error.file.notfound") + ": " + inputFile.getAbsolutePath());
        }

        System.out.print(getMessage("prompt.output.decrypted") + ": ");
        File outputFile = new File(scanner.nextLine().trim());

        System.out.print(getMessage("prompt.key.decrypt") + ": ");
        String keyString = scanner.nextLine();
        if (keyString == null || keyString.isEmpty()) {
            LOGGER.severe("Empty key provided");
            throw new IllegalArgumentException(getMessage("error.key.empty"));
        }

        FileManager.decryptFile(inputFile, outputFile, keyString);
        System.out.println(getMessage("success.decrypted") + ": " + outputFile.getAbsolutePath());
        LOGGER.info("File decrypted: " + outputFile.getAbsolutePath());
    }

    private static void doTagCount() throws Exception {
        System.out.print(getMessage("prompt.url") + ": ");
        String url = scanner.nextLine().trim();
        Map<String, Integer> frequencyMap = TagCounter.countTags(url);

        System.out.println("\n" + getMessage("tags.sorted.name") + ":");
        List<Map.Entry<String, Integer>> byName = TagCounter.sortedByName(frequencyMap);
        byName.forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));

        System.out.println("\n" + getMessage("tags.sorted.frequency") + ":");
        List<Map.Entry<String, Integer>> byFrequency = TagCounter.sortedByFrequency(frequencyMap);
        byFrequency.forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));

        System.out.print(getMessage("prompt.output.tags") + ": ");
        String outputPath = scanner.nextLine().trim();
        File outputFile = new File(outputPath);
        FileManager.serializeObject(outputFile, (Serializable) frequencyMap);
        System.out.println(getMessage("success.serialized") + ": " + outputFile.getAbsolutePath());
        LOGGER.info("Serialized tag frequency to: " + outputFile.getAbsolutePath());
    }

    private static void doFilterWriterDemo() throws Exception {
        System.out.print(getMessage("prompt.demo.output") + ": ");
        File demoOutputFile = new File(scanner.nextLine().trim());
        if (demoOutputFile.exists() && !demoOutputFile.canWrite()) {
            LOGGER.severe("Cannot write to file: " + demoOutputFile.getAbsolutePath());
            throw new IOException(getMessage("error.file.cannotwrite") + ": " + demoOutputFile.getAbsolutePath());
        }

        System.out.print(getMessage("prompt.key") + ": ");
        String keyString = scanner.nextLine();
        if (keyString == null || keyString.isEmpty()) {
            LOGGER.severe("Empty key provided");
            throw new IllegalArgumentException(getMessage("error.key.empty"));
        }

        System.out.println(getMessage("prompt.text.input"));
        String inputLine = scanner.nextLine();

        FileManager.writeEncryptedText(demoOutputFile, inputLine, keyString);
        System.out.println(getMessage("success.written") + ": " + demoOutputFile.getAbsolutePath());
        LOGGER.info("Encrypted text written to: " + demoOutputFile.getAbsolutePath());

        System.out.println(getMessage("demo.reading"));
        String decrypted = FileManager.readEncryptedText(demoOutputFile, keyString);
        System.out.println(getMessage("demo.decrypted") + ": " + decrypted);
        LOGGER.info("Decrypted content read successfully");
    }

    private static void doDeserializeObject() throws Exception {
        System.out.print(getMessage("prompt.input.deserialize") + ": ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            LOGGER.severe("File not found: " + inputFile.getAbsolutePath());
            throw new FileNotFoundException(getMessage("error.file.notfound") + ": " + inputFile.getAbsolutePath());
        }

        Object deserializedObject = FileManager.deserializeObject(inputFile);
        System.out.println(getMessage("success.deserialized"));
        System.out.println(getMessage("object.class") + ": " + deserializedObject.getClass().getName());
        System.out.println(getMessage("object.data") + ": " + deserializedObject);
        LOGGER.info("Deserialized object: " + deserializedObject.getClass().getName());
    }
}