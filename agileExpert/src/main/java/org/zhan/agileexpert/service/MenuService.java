package org.zhan.agileexpert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhan.agileexpert.constants.MenuItemType;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.entity.Menu;
import org.zhan.agileexpert.entity.MenuItem;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.repository.AppRepository;
import org.zhan.agileexpert.repository.UserRepository;
import org.zhan.agileexpert.util.ValidationUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final UserRepository userRepository;
    private final AppRepository appRepository;

    @Transactional(readOnly = true)
    public Menu getMenuForUser(String userId) {
        return getUser(userId).getMenu();
    }


    @Transactional(readOnly = true)
    public List<MenuNodeView> listMenuItems(String userId) {
        List<MenuNodeView> views = new ArrayList<>();
        for (MenuItem item : getMenuForUser(userId).getRootItems()) {
            appendMenuNode(views, item, 0);
        }
        return views;
    }

    @Transactional(readOnly = true)
    public List<MenuNodeView> listSubMenus(String userId) {
        List<MenuNodeView> subMenus = new ArrayList<>();
        for (MenuNodeView item : listMenuItems(userId)) {
            if (item.type() == MenuItemType.SUBMENU) {
                subMenus.add(item);
            }
        }
        return subMenus;
    }

    @Transactional(readOnly = true)
    public List<MenuNodeView> listRootItems(String userId) {
        List<MenuNodeView> views = new ArrayList<>();
        for (MenuItem item : getMenuForUser(userId).getRootItems()) {
            views.add(new MenuNodeView(item.getId(), item.getName(), item.getType(), 0));
        }
        return views;
    }

    @Transactional(readOnly = true)
    public List<MenuNodeView> listChildItems(String userId, String parentMenuItemId) {
        MenuItem parent = findMenuItem(getMenuForUser(userId), parentMenuItemId);
        if (parent.getType() != MenuItemType.SUBMENU) {
            throw new IllegalStateException("Only SUBMENU items can be opened.");
        }

        List<MenuNodeView> views = new ArrayList<>();
        for (MenuItem child : parent.getChildren()) {
            views.add(new MenuNodeView(child.getId(), child.getName(), child.getType(), 0));
        }
        return views;
    }

    @Transactional(readOnly = true)
    public String getParentMenuItemId(String userId, String menuItemId) {
        MenuItem item = findMenuItem(getMenuForUser(userId), menuItemId);
        return item.getParent() == null ? null : item.getParent().getId();
    }

    @Transactional(readOnly = true)
    public boolean containsAppShortcut(String userId, String appId) {
        return containsApp(getMenuForUser(userId).getRootItems(), appId);
    }

    public MenuItem createRootSubMenu(String userId, String submenuName) {
        return createSubMenu(userId, null, submenuName);
    }

    public MenuItem createSubMenu(String userId, String parentMenuItemId, String submenuName) {
        User user = getUser(userId);
        MenuItem submenu = createSubMenuItem(ValidationUtils.requireNonBlank(submenuName, "Submenu name"));
        if (parentMenuItemId == null || parentMenuItemId.isBlank()) {
            user.getMenu().addRootItem(submenu);
        } else {
            MenuItem parent = findMenuItem(user.getMenu(), parentMenuItemId);
            if (parent.getType() != MenuItemType.SUBMENU) {
                throw new IllegalStateException("Only SUBMENU items can contain children.");
            }
            parent.addChild(submenu);
        }
        userRepository.saveAndFlush(user);
        return submenu;
    }

    public MenuItem addRootAppShortcut(String userId, String appId) {
        User user = getUser(userId);
        App app = getApp(appId);
        ensureAppNotPresent(user.getMenu(), appId);
        MenuItem shortcut = createAppShortcut(app);
        user.getMenu().addRootItem(shortcut);
        userRepository.saveAndFlush(user);
        return shortcut;
    }

    public MenuItem addAppShortcutToSubMenu(String userId, String submenuId, String appId) {
        User user = getUser(userId);
        App app = getApp(appId);
        ensureAppNotPresent(user.getMenu(), appId);

        MenuItem submenu = findMenuItem(user.getMenu(), submenuId);
        if (submenu.getType() != MenuItemType.SUBMENU) {
            throw new IllegalStateException("Only SUBMENU items can contain children.");
        }

        MenuItem shortcut = createAppShortcut(app);
        submenu.addChild(shortcut);
        userRepository.saveAndFlush(user);
        return shortcut;
    }

    public void deleteMenuItem(String userId, String menuItemId) {
        User user = getUser(userId);
        if (removeFromList(user.getMenu().getRootItems(), menuItemId)) {
            userRepository.saveAndFlush(user);
            return;
        }
        throw new IllegalArgumentException("Menu item not found: " + menuItemId);
    }

    public MenuItem renameMenuItem(String userId, String menuItemId, String newName) {
        User user = getUser(userId);
        MenuItem item = findMenuItem(user.getMenu(), menuItemId);
        item.setName(ValidationUtils.requireNonBlank(newName, "Menu item name"));
        userRepository.saveAndFlush(user);
        return item;
    }

    @Transactional(readOnly = true)
    public String launchApp(String userId, String menuItemId) {
        MenuItem item = findMenuItem(getMenuForUser(userId), menuItemId);
        if (item.getType() != MenuItemType.APP) {
            throw new IllegalStateException("Only APP menu items can be launched.");
        }
        return item.getApp().getLaunchMessage();
    }

    private void appendMenuNode(List<MenuNodeView> views, MenuItem item, int depth) {
        views.add(new MenuNodeView(item.getId(), item.getName(), item.getType(), depth));
        for (MenuItem child : item.getChildren()) {
            appendMenuNode(views, child, depth + 1);
        }
    }

    private void ensureAppNotPresent(Menu menu, String appId) {
        if (containsApp(menu.getRootItems(), appId)) {
            throw new IllegalStateException("App already exists in this menu tree.");
        }
    }

    private boolean containsApp(List<MenuItem> items, String appId) {
        for (MenuItem item : items) {
            if (item.getType() == MenuItemType.APP && item.getApp() != null
                    && item.getApp().getId().equals(appId)) {
                return true;
            }
            if (containsApp(item.getChildren(), appId)) {
                return true;
            }
        }
        return false;
    }

    private boolean removeFromList(List<MenuItem> items, String menuItemId) {
        for (int index = 0; index < items.size(); index++) {
            MenuItem item = items.get(index);
            if (item.getId().equals(menuItemId)) {
                items.remove(index);
                return true;
            }
            if (removeFromList(item.getChildren(), menuItemId)) {
                return true;
            }
        }
        return false;
    }

    private MenuItem findMenuItem(Menu menu, String menuItemId) {
        MenuItem found = findMenuItemOrNull(menu.getRootItems(), menuItemId);
        if (found != null) {
            return found;
        }
        throw new IllegalArgumentException("Menu item not found: " + menuItemId);
    }

    private MenuItem findMenuItemOrNull(List<MenuItem> items, String menuItemId) {
        for (MenuItem item : items) {
            if (item.getId().equals(menuItemId)) {
                return item;
            }
            MenuItem nested = findMenuItemOrNull(item.getChildren(), menuItemId);
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private MenuItem createSubMenuItem(String name) {
        MenuItem submenu = new MenuItem();
        submenu.setId(UUID.randomUUID().toString());
        submenu.setName(name);
        submenu.setType(MenuItemType.SUBMENU);
        return submenu;
    }

    private MenuItem createAppShortcut(App app) {
        MenuItem shortcut = new MenuItem();
        shortcut.setId(UUID.randomUUID().toString());
        shortcut.setName(app.getName());
        shortcut.setType(MenuItemType.APP);
        shortcut.setApp(app);
        return shortcut;
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private App getApp(String appId) {
        return appRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("App not found: " + appId));
    }

    public record MenuNodeView(String id, String name, MenuItemType type, int depth) {
    }
}
