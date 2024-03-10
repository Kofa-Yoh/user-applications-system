package com.kotkina.userapplicationssystem.web.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponseListPage {

    private List<ApplicationResponse> applications = new ArrayList<>();
    private int currentPage = 0;
    private long totalItems = 0;
    private int totalPages = 0;
    private String sorting = "UNSORTED";
}
