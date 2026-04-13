package org.zhan.agileexpert.cli;

import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.service.UserService;

@Component
@ConditionalOnProperty(name = "agileexpert.cli.enabled", havingValue = "true")
@RequiredArgsConstructor
@Order(1)
public class AgileExpertCliRunner implements CommandLineRunner {

    private final CliConsole cliConsole;
    private final UserService userService;
    private final UserCliHandler userCliHandler;
    private final ResourceCliHandler resourceCliHandler;
    private final MenuCliHandler menuCliHandler;

    @Override
    public void run(String... args) {
        CliSession cliSession = new CliSession(userService);
        User initialUser = userService.listUsers().stream().findFirst().orElse(null);
        if (initialUser != null) {
            cliSession.setCurrentUserId(initialUser.getId());
        }

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printHeader(cliSession);
                printCommands();
                int choice = cliConsole.readInt(scanner, "Choose an option: ");
                System.out.println();

                try {
                    switch (choice) {
                        case 1 -> userCliHandler.showUsers(cliSession);
                        case 2 -> userCliHandler.createUser(cliSession, scanner);
                        case 3 -> userCliHandler.renameUser(cliSession, scanner);
                        case 4 -> userCliHandler.deleteUser(cliSession, scanner);
                        case 5 -> userCliHandler.switchCurrentUser(cliSession, scanner);
                        case 6 -> menuCliHandler.openCurrentMenu(cliSession, scanner);
                        case 7 -> resourceCliHandler.createApp(scanner);
                        case 8 -> resourceCliHandler.updateApp(scanner);
                        case 9 -> resourceCliHandler.deleteApp(scanner);
                        case 10 -> resourceCliHandler.showApps();
                        case 11 -> resourceCliHandler.setTheme(cliSession, scanner);
                        case 12 -> resourceCliHandler.createWallpaper(scanner);
                        case 13 -> resourceCliHandler.setWallpaper(cliSession, scanner);
                        case 14 -> menuCliHandler.createSubMenu(cliSession, scanner);
                        case 15 -> menuCliHandler.addAppShortcut(cliSession, scanner);
                        case 16 -> menuCliHandler.deleteMenuItem(cliSession, scanner);
                        case 0 -> {
                            running = false;
                            System.out.println("Goodbye.");
                        }
                        default -> System.out.println("Unknown option. Please choose a listed command.");
                    }
                } catch (RuntimeException exception) {
                    System.out.println("Operation failed: " + exception.getMessage());
                }

                if (running) {
                    cliConsole.pause(scanner);
                }
            }
        }
    }

    private void printHeader(CliSession cliSession) {
        System.out.println("====================================================");
        System.out.println(" AgileExpert Smart Device Menu System");
        System.out.println("====================================================");

        User currentUser = cliSession.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Current user: (none)");
            System.out.println("Theme: -");
            System.out.println("Wallpaper: -");
        } else {
            System.out.println("Current user: " + currentUser.getName());
            System.out.println(
                    "Theme: " + cliConsole.displayName(currentUser.getTheme() == null ? null : currentUser.getTheme().getName()));
            System.out.println("Wallpaper: "
                    + cliConsole.displayName(
                            currentUser.getWallpaper() == null ? null : currentUser.getWallpaper().getName()));
        }
        System.out.println("====================================================");
    }

    private void printCommands() {
        System.out.println("1. Show all users");
        System.out.println("2. Create user");
        System.out.println("3. Rename user");
        System.out.println("4. Delete user");
        System.out.println("5. Switch current user");
        System.out.println("6. Show current user's menu");
        System.out.println("7. Create app");
        System.out.println("8. Update app");
        System.out.println("9. Delete app");
        System.out.println("10. Show all apps");
        System.out.println("11. Set theme");
        System.out.println("12. Create wallpaper");
        System.out.println("13. Set wallpaper");
        System.out.println("14. Create submenu");
        System.out.println("15. Add app to menu or submenu");
        System.out.println("16. Delete menu item");
        System.out.println("0. Exit");
    }
}
