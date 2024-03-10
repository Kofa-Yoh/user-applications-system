package com.kotkina.userapplicationssystem.web.models.response;

import com.kotkina.userapplicationssystem.entities.RoleType;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String name;
    private String phone;
    private Set<RoleType> roles;
}
