package org.zhan.agileexpert.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zhan.agileexpert.entity.Wallpaper;
import org.zhan.agileexpert.util.ValidationUtils;

@Service
@RequiredArgsConstructor
public class WallpaperStorageService {

    private final CustomizationService customizationService;

    @Value("${agileexpert.storage.wallpapers-dir:uploads/wallpapers}")
    private String wallpapersDir;

    public Wallpaper uploadWallpaper(MultipartFile file, String wallpaperName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Wallpaper file cannot be empty.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Wallpaper must be an image file.");
        }

        String originalFilename = file.getOriginalFilename() == null ? "wallpaper" : file.getOriginalFilename();
        String storedFileName = buildStoredFileName(originalFilename);
        String displayName = wallpaperName == null || wallpaperName.isBlank()
                ? stripExtension(cleanFileName(originalFilename))
                : ValidationUtils.requireNonBlank(wallpaperName, "Wallpaper name");

        Path directory = getWallpaperDirectoryPath();
        Path target = directory.resolve(storedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store wallpaper file.", exception);
        }

        try {
            // Store a browser-friendly URL in the database, not the disk path.
            return customizationService.createWallpaper(displayName, "/uploads/wallpapers/" + storedFileName);
        } catch (RuntimeException exception) {
            // If the database write fails, remove the copied file as well.
            deleteStoredFile(target);
            throw exception;
        }
    }

    public Path getWallpaperDirectory() {
        return getWallpaperDirectoryPath();
    }

    private Path getWallpaperDirectoryPath() {
        Path directory = Path.of(wallpapersDir).toAbsolutePath().normalize();
        try {
            // Create the upload folder on demand so fresh environments work without manual setup.
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to prepare wallpaper storage directory.", exception);
        }
        return directory;
    }

    private String cleanFileName(String filename) {
        String trimmed = filename.trim().replace('\\', '_').replace('/', '_');
        String safe = trimmed.replaceAll("[^a-zA-Z0-9._-]", "-");
        return safe.isBlank() ? "wallpaper.jpg" : safe;
    }

    private String buildStoredFileName(String originalFilename) {
        return UUID.randomUUID() + "-" + cleanFileName(originalFilename);
    }

    private String stripExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }

    private void deleteStoredFile(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
        }
    }
}
