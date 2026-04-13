package org.zhan.agileexpert.dto;

import java.util.List;

public record UserResponse(
        String id,
        String name,
        String themeId,
        String wallpaperId,
        List<MenuItemResponse> menu) {
}
