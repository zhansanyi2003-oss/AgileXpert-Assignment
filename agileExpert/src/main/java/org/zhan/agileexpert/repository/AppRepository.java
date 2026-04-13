package org.zhan.agileexpert.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zhan.agileexpert.entity.App;

public interface AppRepository extends JpaRepository<App, String> {
    Optional<App> findByName(String name);

    @Query("""
            select case when count(menuItem) > 0 then true else false end
            from MenuItem menuItem
            where menuItem.app.id = :appId
            """)
    boolean isStillUsed(@Param("appId") String appId);

    @Modifying
    @Query("""
            update MenuItem menuItem
            set menuItem.name = :appName
            where menuItem.app.id = :appId
            """)
    int updateShortcutNames(@Param("appId") String appId, @Param("appName") String appName);
}
