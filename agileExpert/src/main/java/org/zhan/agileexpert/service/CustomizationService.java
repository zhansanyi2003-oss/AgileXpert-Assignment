package org.zhan.agileexpert.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhan.agileexpert.entity.Theme;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.entity.Wallpaper;
import org.zhan.agileexpert.repository.ThemeRepository;
import org.zhan.agileexpert.repository.UserRepository;
import org.zhan.agileexpert.repository.WallpaperRepository;
import org.zhan.agileexpert.util.ValidationUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomizationService {

    public static final String THEME_LIGHT = "Light";
    public static final String THEME_DARK = "Dark";
    public static final String WALLPAPER_DEFAULT_BLUE = "Default Blue";

    private static final Set<String> SYSTEM_THEME_NAMES = Set.of(THEME_LIGHT, THEME_DARK);

    private final ThemeRepository themeRepository;
    private final WallpaperRepository wallpaperRepository;
    private final UserRepository userRepository;

    public Theme createTheme(String name) {
        String normalizedName = ValidationUtils.requireNonBlank(name, "Theme name");
        if (!SYSTEM_THEME_NAMES.contains(normalizedName)) {
            throw new IllegalArgumentException("Only Light and Dark themes are supported.");
        }
        themeRepository.findByName(normalizedName).ifPresent(existing -> {
            throw new IllegalStateException("Theme already exists: " + normalizedName);
        });

        Theme theme = new Theme();
        theme.setName(normalizedName);
        return themeRepository.save(theme);
    }

    public Theme ensureTheme(String name) {
        return themeRepository.findByName(name)
                .orElseGet(() -> createTheme(name));
    }

    @Transactional(readOnly = true)
    public List<Theme> listThemes() {
        return themeRepository.findAll().stream()
                .toList();
    }

    public Wallpaper createWallpaper(String name, String path) {
        String normalizedName = ValidationUtils.requireNonBlank(name, "Wallpaper name");
        wallpaperRepository.findByName(normalizedName).ifPresent(existing -> {
            throw new IllegalStateException("Wallpaper already exists: " + normalizedName);
        });

        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setName(normalizedName);
        wallpaper.setPath(ValidationUtils.requireNonBlank(path, "Wallpaper path"));
        return wallpaperRepository.save(wallpaper);
    }

    public Wallpaper ensureBuiltInWallpaper(String name) {
        String normalizedName = ValidationUtils.requireNonBlank(name, "Wallpaper name");
        return wallpaperRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    Wallpaper wallpaper = new Wallpaper();
                    wallpaper.setName(normalizedName);
                    wallpaper.setPath("");
                    return wallpaperRepository.save(wallpaper);
                });
    }



    @Transactional(readOnly = true)
    public List<Wallpaper> listWallpapers() {
        return wallpaperRepository.findAll();
    }

    public User setThemeForUser(String userId, String themeId) {
        User user = getUser(userId);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Theme not found: " + themeId));
        if (!SYSTEM_THEME_NAMES.contains(theme.getName())) {
            throw new IllegalArgumentException("Only Light and Dark themes are supported.");
        }
        user.setTheme(theme);
        return userRepository.save(user);
    }

    public User setWallpaperForUser(String userId, String wallpaperId) {
        User user = getUser(userId);
        Wallpaper wallpaper = wallpaperRepository.findById(wallpaperId)
                .orElseThrow(() -> new IllegalArgumentException("Wallpaper not found: " + wallpaperId));
        user.setWallpaper(wallpaper);
        return userRepository.save(user);
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
