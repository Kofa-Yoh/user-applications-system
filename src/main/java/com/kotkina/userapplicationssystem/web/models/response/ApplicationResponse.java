package com.kotkina.userapplicationssystem.web.models.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private String status;
    private ApplicationUserResponse user;
    private String text;
    private String createdOn;
    private String updatedOn;
}
