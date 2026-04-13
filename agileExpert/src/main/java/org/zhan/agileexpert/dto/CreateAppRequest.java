package org.zhan.agileexpert.dto;

public record CreateAppRequest(
        String name,
        String iconName,
        String launchMessage) {
}
