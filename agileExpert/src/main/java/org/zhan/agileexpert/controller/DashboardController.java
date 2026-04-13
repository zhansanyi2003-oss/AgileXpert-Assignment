package org.zhan.agileexpert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhan.agileexpert.common.Result;
import org.zhan.agileexpert.dto.DashboardResponse;
import org.zhan.agileexpert.mapper.ApiMapper;
import org.zhan.agileexpert.service.AppService;
import org.zhan.agileexpert.service.CustomizationService;
import org.zhan.agileexpert.service.SimulationService;
import org.zhan.agileexpert.service.UserService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final AppService appService;
    private final CustomizationService customizationService;
    private final SimulationService simulationService;
    private final ApiMapper apiMapper;

    @GetMapping
    public Result<DashboardResponse> getDashboard() {
        return Result.success(apiMapper.toDashboardResponse(
                userService.listUsers(),
                appService.listApps(),
                customizationService.listThemes(),
                customizationService.listWallpapers()));
    }

    @PostMapping("/simulation")
    public Result<DashboardResponse> runSimulation() {
        simulationService.loadSimulationData();
        return Result.success(apiMapper.toDashboardResponse(
                userService.listUsers(),
                appService.listApps(),
                customizationService.listThemes(),
                customizationService.listWallpapers()));
    }
}
