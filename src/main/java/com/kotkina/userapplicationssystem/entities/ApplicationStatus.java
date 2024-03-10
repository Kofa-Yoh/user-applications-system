package com.kotkina.userapplicationssystem.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ApplicationStatus {
    DRAFT("черновик"), SENT("отправлено"), ACCEPTED("принято"), REJECTED("отклонено");

    private String text;

    private static final Map<String, ApplicationStatus> ENUM_MAP;

    static {
        Map<String, ApplicationStatus> map = new HashMap<>();
        for (ApplicationStatus value : ApplicationStatus.values()) {
            map.put(value.name().toUpperCase(), value);
        }
        ENUM_MAP = map;
    }

    public static ApplicationStatus get(String name) {
        return ENUM_MAP.getOrDefault(name.toUpperCase(), null);
    }
}
