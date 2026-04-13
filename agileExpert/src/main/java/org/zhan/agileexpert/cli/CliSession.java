package org.zhan.agileexpert.cli;

import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.service.UserService;

public class CliSession {

    private final UserService userService;
    @Getter
    @Setter
    private String currentUserId;

    public CliSession(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        if (currentUserId == null || currentUserId.isBlank()) {
            return null;
        }

        try {
            return userService.getUser(currentUserId);
        } catch (IllegalArgumentException exception) {
            currentUserId = null;
            return null;
        }
    }

    public User requireCurrentUser() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No current user selected.");
        }
        return currentUser;
    }

    public List<User> sortedUsers() {
        return userService.listUsers().stream()
                .sorted(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
