package com.kotkina.userapplicationssystem.web.controllers;

import com.kotkina.userapplicationssystem.entities.RoleType;
import com.kotkina.userapplicationssystem.exceptions.RequestNotAvailableException;
import com.kotkina.userapplicationssystem.services.UserService;
import com.kotkina.userapplicationssystem.utils.UserMapper;
import com.kotkina.userapplicationssystem.web.models.response.UserResponse;
import com.kotkina.userapplicationssystem.web.models.response.UserResponseList;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RolesAllowed("ADMIN")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping({"/", ""})
    public ResponseEntity<UserResponseList> getUsers() {
        return ResponseEntity.ok(userMapper.userListToResponseList(userService.getUsersAll()));
    }

    @PutMapping("/{phone}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable String phone,
                                                       @RequestParam("change") String newRole) {
        if (newRole.toUpperCase().equals("OPERATOR")) {
            return ResponseEntity.ok(userMapper.userToResponse(
                    userService.addRoleToUser(phone, RoleType.ROLE_OPERATOR)));
        }
        throw new RequestNotAvailableException("Пользователю можно добавить только роль \"OPERATOR\".");
    }
}
