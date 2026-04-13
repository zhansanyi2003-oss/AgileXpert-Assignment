package org.zhan.agileexpert.service;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DefaultMenuTemplateService {

    public List<AppTemplate> appTemplates() {
        return List.of(
                new AppTemplate("Camera", "camera-icon", "Camera started"),
                new AppTemplate("Google", "google-icon", "Google started"),
                new AppTemplate("Valorant", "valorant-icon", "Valorant started"),
                new AppTemplate("Messenger", "messenger-icon", "Messenger started"),
                new AppTemplate("Word", "word-icon", "Word started"));
    }

    public List<String> defaultRootAppNames() {
        return List.of("Camera", "Google");
    }

    public List<SubMenuTemplate> defaultSubMenus() {
        return List.of(
                new SubMenuTemplate("Games", List.of("Valorant")),
                new SubMenuTemplate("Social", List.of("Messenger")),
                new SubMenuTemplate("Tools", List.of("Word")));
    }

    public record AppTemplate(String name, String iconName, String launchMessage) {
    }

    public record SubMenuTemplate(String name, List<String> appNames) {
    }
}
