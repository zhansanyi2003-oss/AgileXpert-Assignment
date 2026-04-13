package org.zhan.agileexpert.dto;

import java.util.List;
import org.zhan.agileexpert.constants.MenuItemType;

public record MenuItemResponse(
        String id,
        String name,
        MenuItemType type,
        String appId,
        List<MenuItemResponse> children) {
}
