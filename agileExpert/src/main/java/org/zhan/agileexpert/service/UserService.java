package org.zhan.agileexpert.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.entity.Menu;
import org.zhan.agileexpert.entity.MenuItem;
import org.zhan.agileexpert.entity.Theme;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.entity.Wallpaper;
import org.zhan.agileexpert.repository.UserRepository;
import org.zhan.agileexpert.util.ValidationUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private static final long MAX_USERS = 5;

    private final UserRepository userRepository;
    private final AppService appService;
    private final MenuService menuService;
    private final CustomizationService customizationService;
    private final DefaultMenuTemplateService defaultMenuTemplateService;

    public User createUser(String name) {
        String normalizedName = ValidationUtils.requireNonBlank(name, "User name");

        if (userRepository.count() >= MAX_USERS) {
            throw new IllegalStateException("User limit reached. Please recharge to add more users.");
        }

        userRepository.findByName(normalizedName).ifPresent(existing -> {
            throw new IllegalStateException("User already exists: " + normalizedName);
        });

        User user = new User();
        user.setName(normalizedName);

        Menu menu = new Menu();
        menu.setName(normalizedName + "'s Menu");
        user.setMenu(menu);

        // New users always start from the same system defaults.
        Theme defaultTheme = ensureDefaultThemeResources();
        Wallpaper defaultWallpaper = ensureDefaultWallpaperResources();
        ensureDefaultApps();
        user.setTheme(defaultTheme);
        user.setWallpaper(defaultWallpaper);

        User savedUser = userRepository.saveAndFlush(user);
        populateDefaultMenu(savedUser.getId());
        return getUser(savedUser.getId());
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

  
    @Transactional(readOnly = true)
    public Optional<User> findUserByName(String userName) {
        return userRepository.findByName(userName);
    }

    public User renameUser(String userId, String newName) {
        User user = getUser(userId);
        String normalizedName = ValidationUtils.requireNonBlank(newName, "User name");

        userRepository.findByName(normalizedName)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("User already exists: " + normalizedName);
                });

        user.setName(normalizedName);
        if (user.getMenu() != null) {
            user.getMenu().setName(normalizedName + "'s Menu");
        }
        return userRepository.saveAndFlush(user);
    }

    public void deleteUser(String userId) {
        User user = getUser(userId);
        if (userRepository.count() <= 1) {
            throw new IllegalStateException("Cannot delete the last user.");
        }
        userRepository.delete(user);
    }

    private void populateDefaultMenu(String userId) {
        // Root apps and folders come from the shared default menu template.
        for (String appName : defaultMenuTemplateService.defaultRootAppNames()) {
            App app = appService.getAppByName(appName);
            if (!menuService.containsAppShortcut(userId, app.getId())) {
                menuService.addRootAppShortcut(userId, app.getId());
            }
        }
        for (DefaultMenuTemplateService.SubMenuTemplate template : defaultMenuTemplateService.defaultSubMenus()) {
            MenuItem submenu = menuService.createRootSubMenu(userId, template.name());
            for (String appName : template.appNames()) {
                App app = appService.getAppByName(appName);
                if (!menuService.containsAppShortcut(userId, app.getId())) {
                    menuService.addAppShortcutToSubMenu(userId, submenu.getId(), app.getId());
                }
            }
        }
    }

    private void ensureDefaultApps() {
        for (DefaultMenuTemplateService.AppTemplate template : defaultMenuTemplateService.appTemplates()) {
            appService.ensureApp(template.name(), template.iconName(), template.launchMessage());
        }
    }

    private Theme ensureDefaultThemeResources() {
        Theme light = customizationService.ensureTheme(CustomizationService.THEME_LIGHT);
        customizationService.ensureTheme(CustomizationService.THEME_DARK);
        return light;
    }

    private Wallpaper ensureDefaultWallpaperResources() {
        return customizationService.ensureBuiltInWallpaper(CustomizationService.WALLPAPER_DEFAULT_BLUE);
    }
}
