package com.kotkina.userapplicationssystem.utils;

import com.kotkina.userapplicationssystem.entities.User;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationUserResponse;
import com.kotkina.userapplicationssystem.web.models.response.UserResponse;
import com.kotkina.userapplicationssystem.web.models.response.UserResponseList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public UserResponse userToResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .roles(user.getRoles())
                .build();
    }

    public ApplicationUserResponse userToApplicationUserResponse(User user) {
        if (user == null) return null;

        return ApplicationUserResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }

    public List<UserResponse> getUserResponseList(List<User> users) {
        if (users == null) return new ArrayList<>();

        return users.stream()
                .map(this::userToResponse)
                .toList();
    }

    public UserResponseList userListToResponseList(List<User> users) {
        if (users == null) return new UserResponseList();
        if (users.size() == 0) return new UserResponseList();

        return new UserResponseList(getUserResponseList(users));
    }
}
