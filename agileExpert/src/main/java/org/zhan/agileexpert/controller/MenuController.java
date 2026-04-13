package org.zhan.agileexpert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhan.agileexpert.common.Result;
import org.zhan.agileexpert.dto.AddSubmenuAppRequest;
import org.zhan.agileexpert.dto.CreateSubmenuRequest;
import org.zhan.agileexpert.dto.MenuItemResponse;
import org.zhan.agileexpert.dto.NameRequest;
import org.zhan.agileexpert.mapper.ApiMapper;
import org.zhan.agileexpert.service.MenuService;

@RestController
@RequestMapping("/api/users/{userId}/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final ApiMapper apiMapper;

    @PostMapping("/submenus")
    public Result<MenuItemResponse> createSubmenu(
            @PathVariable String userId,
            @RequestBody CreateSubmenuRequest request) {
        return Result.success(apiMapper.toMenuItemResponse(
                menuService.createSubMenu(userId, request.parentMenuItemId(), request.name())));
    }

    @PostMapping("/root-apps/{appId}")
    public Result<MenuItemResponse> addRootApp(
            @PathVariable String userId,
            @PathVariable String appId) {
        return Result.success(apiMapper.toMenuItemResponse(menuService.addRootAppShortcut(userId, appId)));
    }

    @PostMapping("/submenu-apps")
    public Result<MenuItemResponse> addSubmenuApp(
            @PathVariable String userId,
            @RequestBody AddSubmenuAppRequest request) {
        return Result.success(apiMapper.toMenuItemResponse(
                menuService.addAppShortcutToSubMenu(userId, request.submenuId(), request.appId())));
    }

    @PatchMapping("/items/{itemId}")
    public Result<MenuItemResponse> renameMenuItem(
            @PathVariable String userId,
            @PathVariable String itemId,
            @RequestBody NameRequest request) {
        return Result.success(apiMapper.toMenuItemResponse(menuService.renameMenuItem(userId, itemId, request.name())));
    }

    @DeleteMapping("/items/{itemId}")
    public Result<Void> deleteMenuItem(@PathVariable String userId, @PathVariable String itemId) {
        menuService.deleteMenuItem(userId, itemId);
        return Result.success();
    }

    @PostMapping("/items/{itemId}/launch")
    public Result<String> launchApp(@PathVariable String userId, @PathVariable String itemId) {
        return Result.success(menuService.launchApp(userId, itemId));
    }
}
