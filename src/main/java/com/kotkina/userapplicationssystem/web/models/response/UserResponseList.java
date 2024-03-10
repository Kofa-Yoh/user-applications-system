package com.kotkina.userapplicationssystem.web.models.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseList {

    private List<UserResponse> users = new ArrayList<>();
}
