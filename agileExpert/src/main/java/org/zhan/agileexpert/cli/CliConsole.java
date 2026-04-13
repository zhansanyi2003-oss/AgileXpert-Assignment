package org.zhan.agileexpert.cli;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class CliConsole {

    public int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException ignored) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public String readRequiredLine(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (!line.isBlank()) {
                return line.trim();
            }
            System.out.println("Value cannot be blank.");
        }
    }

    public String readLineOrDefault(Scanner scanner, String prompt, String defaultValue) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        if (line == null || line.isBlank()) {
            return defaultValue;
        }
        return line.trim();
    }

    public <T> T chooseFromList(Scanner scanner, List<T> items, Function<T, String> labeler, String prompt) {
        System.out.println(prompt + ":");
        for (int index = 0; index < items.size(); index++) {
            System.out.printf("%d. %s%n", index + 1, labeler.apply(items.get(index)));
        }
        System.out.println("0. Cancel");

        while (true) {
            int choice = readInt(scanner, "Choose: ");
            if (choice == 0) {
                return null;
            }
            int selectedIndex = choice - 1;
            if (selectedIndex >= 0 && selectedIndex < items.size()) {
                return items.get(selectedIndex);
            }
            System.out.println("Please choose a valid item number.");
        }
    }

    public void pause(Scanner scanner) {
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }

    public String indent(int depth) {
        return "  ".repeat(Math.max(depth, 0));
    }

    public String displayName(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
