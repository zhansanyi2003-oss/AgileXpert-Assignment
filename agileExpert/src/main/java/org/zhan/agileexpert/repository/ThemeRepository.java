package org.zhan.agileexpert.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zhan.agileexpert.entity.Theme;

public interface ThemeRepository extends JpaRepository<Theme, String> {
    Optional<Theme> findByName(String name);
}
