package org.zhan.agileexpert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zhan.agileexpert.service.WallpaperStorageService;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    private final WallpaperStorageService wallpaperStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = wallpaperStorageService.getWallpaperDirectory().toUri().toString();
        registry.addResourceHandler("/uploads/wallpapers/**")
                .addResourceLocations(location);
    }
}
