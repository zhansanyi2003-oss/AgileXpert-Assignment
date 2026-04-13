package org.zhan.agileexpert.cli;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.entity.Theme;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.entity.Wallpaper;
import org.zhan.agileexpert.service.AppService;
import org.zhan.agileexpert.service.CustomizationService;

@Component
@RequiredArgsConstructor
public class ResourceCliHandler {

    private final CliConsole cliConsole;
    private final AppService appService;
    private final CustomizationService customizationService;

    public void createApp(Scanner scanner) {
        String name = cliConsole.readRequiredLine(scanner, "App name: ");
        String iconName = cliConsole.readRequiredLine(scanner, "Icon name: ");
        String launchMessage = cliConsole.readRequiredLine(scanner, "Launch message: ");
        App created = appService.createApp(name, iconName, launchMessage);
        System.out.println("Created app: " + created.getName());
    }

    public void updateApp(Scanner scanner) {
        List<App> apps = sortedApps();
        if (apps.isEmpty()) {
            System.out.println("No apps available. Create one first.");
            return;
        }

        App selectedApp = cliConsole.chooseFromList(scanner, apps, App::getName, "Choose an app to update");
        if (selectedApp == null) {
            System.out.println("Update cancelled.");
            return;
        }
        String updatedName = cliConsole.readLineOrDefault(
                scanner, "New app name [" + selectedApp.getName() + "]: ", selectedApp.getName());
        String updatedIconName = cliConsole.readLineOrDefault(
                scanner, "New icon name [" + selectedApp.getIconName() + "]: ", selectedApp.getIconName());
        String updatedLaunchMessage = cliConsole.readLineOrDefault(
                scanner,
                "New launch message [" + selectedApp.getLaunchMessage() + "]: ",
                selectedApp.getLaunchMessage());

        App updated = appService.updateApp(
                selectedApp.getId(),
                updatedName,
                updatedIconName,
                updatedLaunchMessage);
        System.out.println("Updated app: " + updated.getName());
    }

    public void deleteApp(Scanner scanner) {
        List<App> apps = sortedApps();
        if (apps.isEmpty()) {
            System.out.println("No apps available.");
            return;
        }

        App selectedApp = cliConsole.chooseFromList(scanner, apps, App::getName, "Choose an app to delete");
        if (selectedApp == null) {
            System.out.println("Delete cancelled.");
            return;
        }
        appService.deleteApp(selectedApp.getId());
        System.out.println("Deleted app: " + selectedApp.getName());
    }

    public void showApps() {
        List<App> apps = sortedApps();
        if (apps.isEmpty()) {
            System.out.println("No apps available.");
            return;
        }

        System.out.println("Available apps:");
        for (int index = 0; index < apps.size(); index++) {
            App app = apps.get(index);
            System.out.printf("%d. %s (icon: %s)%n", index + 1, app.getName(), app.getIconName());
        }
    }

    public void setTheme(CliSession cliSession, Scanner scanner) {
        User currentUser = cliSession.requireCurrentUser();
        List<Theme> themes = sortedThemes();
        if (themes.isEmpty()) {
            System.out.println("No themes available.");
            return;
        }

        Theme selectedTheme = cliConsole.chooseFromList(scanner, themes, Theme::getName, "Choose a theme");
        if (selectedTheme == null) {
            System.out.println("Theme selection cancelled.");
            return;
        }
        customizationService.setThemeForUser(currentUser.getId(), selectedTheme.getId());
        System.out.println("Theme updated to: " + selectedTheme.getName());
    }

    public void createWallpaper(Scanner scanner) {
        String name = cliConsole.readRequiredLine(scanner, "Wallpaper display name: ");
        String path = cliConsole.readRequiredLine(scanner, "Wallpaper file path: ");
        Wallpaper created = customizationService.createWallpaper(name, path);
        System.out.println("Created wallpaper: " + created.getName());
    }

    public void setWallpaper(CliSession cliSession, Scanner scanner) {
        User currentUser = cliSession.requireCurrentUser();
        List<Wallpaper> wallpapers = sortedWallpapers();
        if (wallpapers.isEmpty()) {
            System.out.println("No wallpapers available. Create one first.");
            return;
        }

        Wallpaper selectedWallpaper =
                cliConsole.chooseFromList(scanner, wallpapers, Wallpaper::getName, "Choose a wallpaper");
        if (selectedWallpaper == null) {
            System.out.println("Wallpaper selection cancelled.");
            return;
        }
        customizationService.setWallpaperForUser(currentUser.getId(), selectedWallpaper.getId());
        System.out.println("Wallpaper updated to: " + selectedWallpaper.getName());
    }

    public List<App> sortedApps() {
        return appService.listApps().stream()
                .sorted(Comparator.comparing(App::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<Theme> sortedThemes() {
        return customizationService.listThemes().stream()
                .sorted(Comparator.comparing(Theme::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<Wallpaper> sortedWallpapers() {
        return customizationService.listWallpapers().stream()
                .sorted(Comparator.comparing(Wallpaper::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
