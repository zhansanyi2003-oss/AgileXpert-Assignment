package org.zhan.agileexpert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhan.agileexpert.common.Result;
import org.zhan.agileexpert.dto.NameRequest;
import org.zhan.agileexpert.dto.UserResponse;
import org.zhan.agileexpert.entity.User;
import org.zhan.agileexpert.mapper.ApiMapper;
import org.zhan.agileexpert.service.CustomizationService;
import org.zhan.agileexpert.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomizationService customizationService;
    private final ApiMapper apiMapper;

    @PostMapping
    public Result<UserResponse> createUser(@RequestBody NameRequest request) {
        return Result.success(apiMapper.toUserResponse(userService.createUser(request.name())));
    }

    @GetMapping("/{userId}")
    public Result<UserResponse> getUser(@PathVariable String userId) {
        return Result.success(apiMapper.toUserResponse(userService.getUser(userId)));
    }

    @PatchMapping("/{userId}")
    public Result<UserResponse> renameUser(
            @PathVariable String userId,
            @RequestBody NameRequest request) {
        return Result.success(apiMapper.toUserResponse(userService.renameUser(userId, request.name())));
    }

    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.success();
    }

    @PutMapping("/{userId}/theme/{themeId}")
    public Result<UserResponse> selectTheme(
            @PathVariable String userId,
            @PathVariable String themeId) {
        User updated = customizationService.setThemeForUser(userId, themeId);
        return Result.success(apiMapper.toUserResponse(updated));
    }

    @PutMapping("/{userId}/wallpaper/{wallpaperId}")
    public Result<UserResponse> selectWallpaper(
            @PathVariable String userId,
            @PathVariable String wallpaperId) {
        User updated = customizationService.setWallpaperForUser(userId, wallpaperId);
        return Result.success(apiMapper.toUserResponse(updated));
    }
}
