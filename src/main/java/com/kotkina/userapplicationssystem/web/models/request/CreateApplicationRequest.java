package com.kotkina.userapplicationssystem.web.models.request;

import com.kotkina.userapplicationssystem.entities.ApplicationStatus;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    private ApplicationStatus status;
    private String text;
}
