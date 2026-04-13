package org.zhan.agileexpert.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.repository.AppRepository;
import org.zhan.agileexpert.util.ValidationUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class AppService {

    private final AppRepository appRepository;

    public App createApp(String name, String iconName, String launchMessage) {
        String normalizedName = ValidationUtils.requireNonBlank(name, "App name");
        appRepository.findByName(normalizedName).ifPresent(existing -> {
            throw new IllegalStateException("App already exists: " + normalizedName);
        });

        App app = new App();
        app.setName(normalizedName);
        app.setIconName(ValidationUtils.requireNonBlank(iconName, "Icon name"));
        app.setLaunchMessage(ValidationUtils.requireNonBlank(launchMessage, "Launch message"));
        return appRepository.save(app);
    }

    public App updateApp(String appId, String name, String iconName, String launchMessage) {
        App app = getApp(appId);
        String normalizedName = ValidationUtils.requireNonBlank(name, "App name");
        appRepository.findByName(normalizedName)
                .filter(existing -> !existing.getId().equals(appId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("App already exists: " + normalizedName);
        });
        app.setName(normalizedName);
        app.setIconName(ValidationUtils.requireNonBlank(iconName, "Icon name"));
        app.setLaunchMessage(ValidationUtils.requireNonBlank(launchMessage, "Launch message"));
        App updated = appRepository.saveAndFlush(app);
        // Existing menu shortcuts keep a copy of the app name for display.
        appRepository.updateShortcutNames(appId, normalizedName);
        return updated;
    }

    @Transactional(readOnly = true)
    public List<App> listApps() {
        return appRepository.findAll();
    }

    @Transactional(readOnly = true)
    public App getApp(String appId) {
        return appRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("App not found: " + appId));
    }

    @Transactional(readOnly = true)
    public App getAppByName(String appName) {
        return appRepository.findByName(appName)
                .orElseThrow(() -> new IllegalArgumentException("App not found: " + appName));
    }

    public void deleteApp(String appId) {
        App app = getApp(appId);
        if (appRepository.isStillUsed(appId)) {
            throw new IllegalStateException("App is still used in one or more menus.");
        }
        appRepository.delete(app);
    }

    public App ensureApp(String name, String iconName, String launchMessage) {
        return appRepository.findByName(name)
                .orElseGet(() -> createApp(name, iconName, launchMessage));
    }
}
