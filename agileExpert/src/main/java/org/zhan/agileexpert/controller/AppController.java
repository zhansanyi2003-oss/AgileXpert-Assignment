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
import org.zhan.agileexpert.dto.AppResponse;
import org.zhan.agileexpert.dto.CreateAppRequest;
import org.zhan.agileexpert.mapper.ApiMapper;
import org.zhan.agileexpert.service.AppService;

@RestController
@RequestMapping("/api/apps")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final ApiMapper apiMapper;

    @PostMapping
    public Result<AppResponse> createApp(@RequestBody CreateAppRequest request) {
        return Result.success(apiMapper.toAppResponse(
                appService.createApp(request.name(), request.iconName(), request.launchMessage())));
    }

    @PatchMapping("/{appId}")
    public Result<AppResponse> updateApp(@PathVariable String appId, @RequestBody CreateAppRequest request) {
        return Result.success(apiMapper.toAppResponse(
                appService.updateApp(appId, request.name(), request.iconName(), request.launchMessage())));
    }

    @DeleteMapping("/{appId}")
    public Result<Void> deleteApp(@PathVariable String appId) {
        appService.deleteApp(appId);
        return Result.success();
    }
}
