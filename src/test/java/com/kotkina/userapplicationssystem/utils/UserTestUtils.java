package com.kotkina.userapplicationssystem.utils;

import com.kotkina.userapplicationssystem.entities.RoleType;
import com.kotkina.userapplicationssystem.entities.User;

import java.util.Set;

public class UserTestUtils {

    public static User createUser(Long id, String username, String name, RoleType[] roles) {
        return User.builder()
                .id(id)
                .username(username)
                .name(name)
                .password("123")
                .countryCode(7)
                .cityCode(952)
                .phoneNumber("1111111")
                .roles(Set.of(roles))
                .build();
    }
}
