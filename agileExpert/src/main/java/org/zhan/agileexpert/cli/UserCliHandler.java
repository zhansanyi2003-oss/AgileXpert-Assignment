package org.zhan.agileexpert.cli;

import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.service.UserService;

@Component
@RequiredArgsConstructor
public class UserCliHandler {

    private final CliConsole cliConsole;
    private final UserService userService;

    public void showUsers(CliSession cliSession) {
        List<User> users = cliSession.sortedUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        String currentUserId = cliSession.getCurrentUserId();
        System.out.println("Users:");
        for (int index = 0; index < users.size(); index++) {
            User user = users.get(index);
            String marker = user.getId().equals(currentUserId) ? " (current)" : "";
            System.out.printf("%d. %s%s%n", index + 1, user.getName(), marker);
        }
    }

    public void createUser(CliSession cliSession, Scanner scanner) {
        String name = cliConsole.readRequiredLine(scanner, "Enter new user name: ");
        User created = userService.createUser(name);
        if (cliSession.getCurrentUserId() == null) {
            cliSession.setCurrentUserId(created.getId());
        }
        System.out.println("Created user: " + created.getName());
    }

    public void renameUser(CliSession cliSession, Scanner scanner) {
        List<User> users = cliSession.sortedUsers();
        if (users.isEmpty()) {
            System.out.println("No users available to rename.");
            return;
        }

        User selectedUser = cliConsole.chooseFromList(scanner, users, User::getName, "Choose a user to rename");
        if (selectedUser == null) {
            System.out.println("Rename cancelled.");
            return;
        }
        String newName = cliConsole.readRequiredLine(scanner, "Enter new user name: ");
        User renamed = userService.renameUser(selectedUser.getId(), newName);
        System.out.println("Renamed user to: " + renamed.getName());
    }

    public void deleteUser(CliSession cliSession, Scanner scanner) {
        List<User> users = cliSession.sortedUsers();
        if (users.isEmpty()) {
            System.out.println("No users available to delete.");
            return;
        }

        User selectedUser = cliConsole.chooseFromList(scanner, users, User::getName, "Choose a user to delete");
        if (selectedUser == null) {
            System.out.println("Delete cancelled.");
            return;
        }
        userService.deleteUser(selectedUser.getId());

        if (selectedUser.getId().equals(cliSession.getCurrentUserId())) {
            List<User> remainingUsers = cliSession.sortedUsers();
            cliSession.setCurrentUserId(remainingUsers.isEmpty() ? null : remainingUsers.get(0).getId());
        }

        System.out.println("Deleted user: " + selectedUser.getName());
    }

    public void switchCurrentUser(CliSession cliSession, Scanner scanner) {
        List<User> users = cliSession.sortedUsers();
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        User selectedUser = cliConsole.chooseFromList(scanner, users, User::getName, "Choose the current user");
        if (selectedUser == null) {
            System.out.println("Switch cancelled.");
            return;
        }
        cliSession.setCurrentUserId(selectedUser.getId());
        System.out.println("Current user switched to: " + selectedUser.getName());
    }
}
