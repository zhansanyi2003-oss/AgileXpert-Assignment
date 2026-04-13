package org.zhan.agileexpert.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zhan.agileexpert.entity.Wallpaper;

public interface WallpaperRepository extends JpaRepository<Wallpaper, String> {
    Optional<Wallpaper> findByName(String name);
}
