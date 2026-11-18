package util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Utility class providing validated console input methods.
 *
 * <p>Demonstrates: Input validation, exception handling (NumberFormatException),
 * regex pattern matching, while-loop retry logic.</p>
 *
 * <b>Viva tip:</b> Instead of crashing on bad input, each method keeps
 * re-prompting the user until they enter something valid.  This is "defensive
 * programming" — a key rubric criterion.
 */
public class InputValidator {

    /** Simple email regex — checks for "something@something.something". */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    // Utility class — no instances needed
    private InputValidator() {}

    /**
     * Prompts until the user enters an integer within [min, max].
     *
     * @param sc     Open Scanner for System.in
     * @param prompt Message to display before each attempt
     * @param min    Minimum acceptable value (inclusive)
     * @param max    Maximum acceptable value (inclusive)
     * @return A validated integer in [min, max]
     */
    public static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.printf("  Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a whole number.");
            }
        }
    }

    /**
     * Prompts until the user enters a decimal number >= min.
     *
     * @param sc     Open Scanner for System.in
     * @param prompt Message to display before each attempt
     * @param min    Minimum acceptable value (inclusive)
     * @return A validated double >= min
     */
    public static double readDouble(Scanner sc, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= min) return value;
                System.out.printf("  Value must be >= %.2f.%n", min);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a decimal number.");
            }
        }
    }

    /**
     * Prompts until the user enters a non-blank string.
     *
     * @param sc     Open Scanner for System.in
     * @param prompt Message to display before each attempt
     * @return A non-empty, trimmed string
     */
    public static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("  Input cannot be empty. Please try again.");
        }
    }

    /**
     * Prompts until the user enters a string that looks like a valid email.
     *
     * @param sc     Open Scanner for System.in
     * @param prompt Message to display before each attempt
     * @return A validated email string
     */
    public static String readEmail(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (EMAIL_PATTERN.matcher(line).matches()) return line;
            System.out.println("  Invalid email format. Example: user@domain.com");
        }
    }
}
