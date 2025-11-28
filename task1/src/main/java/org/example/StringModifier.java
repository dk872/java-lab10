package org.example;

import java.lang.reflect.Field;
import java.util.Scanner;

public class StringModifier {
    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(StringModifier.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a new value for the rows:");
        String replacementValue = scanner.nextLine();
        System.out.println("----------------------------------------");

        String literalString = "Original literal";
        System.out.println("Test 1: String literal");
        System.out.println("   Before the change:   " + literalString);

        modifyStringValue(literalString, replacementValue);

        System.out.println("   After the change: " + literalString);
        System.out.println("----------------------------------------");

        System.out.println("Enter the original string for the second test:");
        String inputString = scanner.nextLine();
        System.out.println("Test 2: String from keyboard");
        System.out.println("   Before the change:   " + inputString);

        modifyStringValue(inputString, replacementValue);

        System.out.println("   After the change: " + inputString);
        System.out.println("----------------------------------------");

        scanner.close();
    }

    private static void modifyStringValue(String targetString, String newValue) {
        try {
            Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);

            Field coderField = String.class.getDeclaredField("coder");
            coderField.setAccessible(true);

            byte[] newValueBytes = (byte[]) valueField.get(newValue);
            byte newCoder = coderField.getByte(newValue);

            valueField.set(targetString, newValueBytes);
            coderField.setByte(targetString, newCoder);

        } catch (NoSuchFieldException e) {
            LOGGER.severe("Error: Field 'value' or 'coder' not found. " + e.getMessage());
        } catch (IllegalAccessException e) {
            LOGGER.severe("Access Error: add JVM argument --add-opens java.base/java.lang=ALL-UNNAMED. "
                    + e.getMessage());
        } catch (Exception e) {
            LOGGER.severe("Unexpected error: " + e.getMessage());
        }
    }
}
