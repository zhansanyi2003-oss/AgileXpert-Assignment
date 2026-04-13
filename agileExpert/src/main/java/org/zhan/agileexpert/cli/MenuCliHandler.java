package org.zhan.agileexpert.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.zhan.agileexpert.constants.MenuItemType;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.service.MenuService;
import org.zhan.agileexpert.service.MenuService.MenuNodeView;

@Component
@RequiredArgsConstructor
public class MenuCliHandler {

    private final CliConsole cliConsole;
    private final ResourceCliHandler resourceCliHandler;
    private final MenuService menuService;

    public void openCurrentMenu(CliSession cliSession, Scanner scanner) {
        User currentUser = cliSession.requireCurrentUser();
        String currentMenuItemId = null;
        List<String> path = new ArrayList<>();

        while (true) {
            List<MenuNodeView> items = currentMenuItemId == null
                    ? menuService.listRootItems(currentUser.getId())
                    : menuService.listChildItems(currentUser.getId(), currentMenuItemId);

            String title = path.isEmpty()
                    ? currentUser.getName() + "'s menu"
                    : currentUser.getName() + "'s menu / " + String.join(" / ", path);

            System.out.println(title + ":");
            if (items.isEmpty()) {
                System.out.println("- (empty)");
            } else {
                for (int index = 0; index < items.size(); index++) {
                    MenuNodeView item = items.get(index);
                    String marker = item.type() == MenuItemType.SUBMENU ? "[submenu]" : "[app]";
                    System.out.printf("%d. %s %s%n", index + 1, marker, item.name());
                }
            }

            System.out.println(currentMenuItemId == null ? "0. Back" : "0. Up");

            int selectedIndex = cliConsole.readInt(scanner, "Choose: ");
            if (selectedIndex == 0) {
                if (currentMenuItemId == null) {
                    return;
                }
                path.remove(path.size() - 1);
                currentMenuItemId = menuService.getParentMenuItemId(currentUser.getId(), currentMenuItemId);
                System.out.println();
                continue;
            }

            if (selectedIndex < 1 || selectedIndex > items.size()) {
                System.out.println("Please choose a valid item number.");
                System.out.println(); 
                continue;
            }

            MenuNodeView selectedItem = items.get(selectedIndex - 1);
            if (selectedItem.type() == MenuItemType.APP) {
                String launchMessage = menuService.launchApp(currentUser.getId(), selectedItem.id());
                System.out.println("Launching " + selectedItem.name() + "...");
                System.out.println(launchMessage);
                return;
            }

            currentMenuItemId = selectedItem.id();
            path.add(selectedItem.name());
            System.out.println();
        }
    }

    public void createSubMenu(CliSession cliSession, Scanner scanner) {
        User currentUser = cliSession.requireCurrentUser();
        String submenuName = cliConsole.readRequiredLine(scanner, "Submenu name: ");
        MenuNodeView created = toView(menuService.createRootSubMenu(currentUser.getId(), submenuName));
        System.out.println("Created submenu: " + created.name());
    }

    public void addAppShortcut(CliSession cliSession, Scanner scanner) {
        User currentUser = cliSession.requireCurrentUser();
        List<App> apps = resourceCliHandler.sortedApps();
        if (apps.isEmpty()) {
            System.out.println("No apps available. Create one first.");
            return;
        }

        App selectedApp = cliConsole.chooseFromList(scanner, apps, App::getName, "Choose an app");
        if (selectedApp == null) {
            System.out.println("Add app cancelled.");
            return;
        }
        int target = cliConsole.readInt(scanner, "Add to 1) root menu, 2) submenu, or 0) cancel? ");
        if (target == 0) {
            System.out.println("Add app cancelled.");
            return;
        }
        if (target == 1) {
            menuService.addRootAppShortcut(currentUser.getId(), selectedApp.getId());
            System.out.println("Added " + selectedApp.getName() + " to the root menu.");
            return;
        }

        if (target == 2) {
            List<MenuNodeView> subMenus = menuService.listSubMenus(currentUser.getId());
            if (subMenus.isEmpty()) {
                System.out.println("No submenus available. Create one first.");
                return;
            }

            MenuNodeView selectedSubMenu = cliConsole.chooseFromList(
                    scanner, subMenus, item -> cliConsole.indent(item.depth()) + item.name(), "Choose a submenu");
            if (selectedSubMenu == null) {
                System.out.println("Add app cancelled.");
                return;
            }
            menuService.addAppShortcutToSubMenu(currentUser.getId(), selectedSubMenu.id(), selectedApp.getId());
            System.out.println("Added " + selectedApp.getName() + " to submenu " + selectedSubMenu.name() + ".");
            return;
        }

        System.out.println("Unknown target. Nothing changed.");
    }

    public void deleteMenuItem(CliSession cliSession, Scanner scanner) {
        User currentUser = cliSession.requireCurrentUser();
        List<MenuNodeView> items = menuService.listMenuItems(currentUser.getId());
        if (items.isEmpty()) {
            System.out.println("Menu is empty.");
            return;
        }

        MenuNodeView selectedItem = cliConsole.chooseFromList(
                scanner, items, item -> cliConsole.indent(item.depth()) + item.name(), "Choose a menu item to delete");
        if (selectedItem == null) {
            System.out.println("Delete cancelled.");
            return;
        }
        menuService.deleteMenuItem(currentUser.getId(), selectedItem.id());
        System.out.println("Deleted menu item: " + selectedItem.name());
    }

    private MenuNodeView toView(org.zhan.agileexpert.entity.MenuItem item) {
        return new MenuNodeView(item.getId(), item.getName(), item.getType(), 0);
    }
}
