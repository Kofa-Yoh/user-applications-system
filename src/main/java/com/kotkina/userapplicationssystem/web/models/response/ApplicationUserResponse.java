package com.kotkina.userapplicationssystem.web.models.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUserResponse {

    private String name;
    private String phone;
}
