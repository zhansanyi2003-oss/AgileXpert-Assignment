package org.zhan.agileexpert.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zhan.agileexpert.dto.AppResponse;
import org.zhan.agileexpert.dto.DashboardResponse;
import org.zhan.agileexpert.dto.MenuItemResponse;
import org.zhan.agileexpert.dto.ThemeResponse;
import org.zhan.agileexpert.dto.UserResponse;
import org.zhan.agileexpert.dto.UserSummaryResponse;
import org.zhan.agileexpert.dto.WallpaperResponse;
import org.zhan.agileexpert.entity.App;
import org.zhan.agileexpert.entity.MenuItem;
import org.zhan.agileexpert.entity.Theme;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.entity.Wallpaper;

@Mapper(componentModel = "spring")
public interface ApiMapper {

    default DashboardResponse toDashboardResponse(
            List<User> users,
            List<App> apps,
            List<Theme> themes,
            List<Wallpaper> wallpapers) {
        return new DashboardResponse(
                toUserSummaryResponses(users),
                toAppResponses(apps),
                toThemeResponses(themes),
                toWallpaperResponses(wallpapers));
    }

    @Mapping(target = "themeId", source = "theme.id")
    @Mapping(target = "wallpaperId", source = "wallpaper.id")
    UserSummaryResponse toUserSummaryResponse(User user);

    @Mapping(target = "themeId", source = "theme.id")
    @Mapping(target = "wallpaperId", source = "wallpaper.id")
    @Mapping(target = "menu", source = "menu.rootItems")
    UserResponse toUserResponse(User user);

    @Mapping(target = "appId", source = "app.id")
    MenuItemResponse toMenuItemResponse(MenuItem item);

    AppResponse toAppResponse(App app);

    ThemeResponse toThemeResponse(Theme theme);

    WallpaperResponse toWallpaperResponse(Wallpaper wallpaper);

    List<UserResponse> toUserResponses(List<User> users);

    List<UserSummaryResponse> toUserSummaryResponses(List<User> users);

    List<MenuItemResponse> toMenuItemResponses(List<MenuItem> items);

    List<AppResponse> toAppResponses(List<App> apps);

    List<ThemeResponse> toThemeResponses(List<Theme> themes);

    List<WallpaperResponse> toWallpaperResponses(List<Wallpaper> wallpapers);
}
