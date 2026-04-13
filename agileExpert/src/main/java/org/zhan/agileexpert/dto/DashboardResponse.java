package org.zhan.agileexpert.dto;

import java.util.List;

public record DashboardResponse(
        List<UserSummaryResponse> users,
        List<AppResponse> apps,
        List<ThemeResponse> themes,
        List<WallpaperResponse> wallpapers) {
}
