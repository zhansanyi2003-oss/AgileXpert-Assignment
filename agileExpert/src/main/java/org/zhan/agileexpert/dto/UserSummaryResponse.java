package org.zhan.agileexpert.dto;

public record UserSummaryResponse(
        String id,
        String name,
        String themeId,
        String wallpaperId) {
}
