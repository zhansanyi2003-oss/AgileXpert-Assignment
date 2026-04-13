package org.zhan.agileexpert.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.entity.MenuItem;
import org.zhan.agileexpert.entity.Theme;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.entity.Wallpaper;
import org.zhan.agileexpert.repository.AppRepository;
import org.zhan.agileexpert.repository.ThemeRepository;
import org.zhan.agileexpert.repository.UserRepository;
import org.zhan.agileexpert.repository.WallpaperRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationService {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final WallpaperRepository wallpaperRepository;
    private final AppRepository appRepository;
    private final UserService userService;
    private final MenuService menuService;
    private final AppService appService;
    private final CustomizationService customizationService;

    public void loadSimulationData() {
        userRepository.deleteAll();
        themeRepository.deleteAll();
        wallpaperRepository.deleteAll();
        appRepository.deleteAll();

        Theme light = customizationService.ensureTheme(CustomizationService.THEME_LIGHT);
        Theme dark = customizationService.ensureTheme(CustomizationService.THEME_DARK);
        Wallpaper defaultBlue = customizationService.ensureBuiltInWallpaper(
                CustomizationService.WALLPAPER_DEFAULT_BLUE);

        User father = userService.createUser("Father");
        customizationService.setThemeForUser(father.getId(), dark.getId());
        customizationService.setWallpaperForUser(father.getId(), defaultBlue.getId());

        User child = userService.createUser("Child");
        customizationService.setThemeForUser(child.getId(), light.getId());
        customizationService.setWallpaperForUser(child.getId(), defaultBlue.getId());
        removeShortcutByName(child.getId(), "Tools");
        MenuItem childPlay = menuService.createRootSubMenu(child.getId(), "Play");
        appService.ensureApp("Roblox", "roblox-icon", "Roblox started");
        addAppIfMissing(child.getId(), childPlay.getId(), "Roblox");

        User mother = userService.createUser("Mother");
        customizationService.setThemeForUser(mother.getId(), light.getId());
        customizationService.setWallpaperForUser(mother.getId(), defaultBlue.getId());
        removeShortcutByName(mother.getId(), "Tools");
        MenuItem recipes = menuService.createRootSubMenu(mother.getId(), "Recipes");
        appService.ensureApp("Recipes", "recipes-icon", "Recipes started");
        addAppIfMissing(mother.getId(), recipes.getId(), "Recipes");
    }

    private void addAppIfMissing(String userId, String submenuId, String appName) {
        App app = appService.getAppByName(appName);
        if (!menuService.containsAppShortcut(userId, app.getId())) {
            menuService.addAppShortcutToSubMenu(userId, submenuId, app.getId());
        }
    }

    private void removeShortcutByName(String userId, String shortcutName) {
        List<MenuService.MenuNodeView> rootItems = menuService.listRootItems(userId);
        rootItems.stream()
                .filter(item -> item.name().equals(shortcutName))
                .findFirst()
                .ifPresent(item -> menuService.deleteMenuItem(userId, item.id()));
    }
}
