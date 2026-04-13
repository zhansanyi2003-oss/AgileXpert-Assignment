package org.zhan.agileexpert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zhan.agileexpert.common.Result;
import org.zhan.agileexpert.dto.WallpaperResponse;
import org.zhan.agileexpert.mapper.ApiMapper;
import org.zhan.agileexpert.service.WallpaperStorageService;

@RestController
@RequestMapping("/api/wallpapers")
@RequiredArgsConstructor
public class WallpaperController {

    private final WallpaperStorageService wallpaperStorageService;
    private final ApiMapper apiMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<WallpaperResponse> uploadWallpaper(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {
        return Result.success(apiMapper.toWallpaperResponse(wallpaperStorageService.uploadWallpaper(file, name)));
    }
}
