package com.kotkina.userapplicationssystem.web.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseBody {

    private String message;
    private String description;
}
