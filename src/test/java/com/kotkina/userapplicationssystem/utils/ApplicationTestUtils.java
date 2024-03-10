package com.kotkina.userapplicationssystem.utils;

import com.kotkina.userapplicationssystem.entities.Application;
import com.kotkina.userapplicationssystem.entities.ApplicationStatus;
import com.kotkina.userapplicationssystem.entities.RoleType;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponse;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponseListPage;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApplicationTestUtils {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Application createApplication(Long id, ApplicationStatus status) {
        return Application.builder()
                .id(id)
                .user(UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER}))
                .status(status)
                .text("New Application")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public static Page<Application> createApplicationsPageWithStatus(ApplicationStatus status, Pageable nextPage) {
        return new PageImpl(List.of(ApplicationTestUtils.createApplication(1L, status)), nextPage, 1);
    }

    public static ApplicationResponse applicationToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .user(new ApplicationUserResponse(application.getUser().getName(), application.getUser().getPhone()))
                .status(application.getStatus().getText())
                .text(application.getText())
                .createdOn(application.getCreatedOn().format(FORMATTER))
                .updatedOn(application.getUpdatedOn().format(FORMATTER))
                .build();
    }

    public static List<ApplicationResponse> listToResponseList(List<Application> applications) {
        return applications.stream()
                .map(a -> applicationToResponse(a))
                .toList();
    }

    public static ApplicationResponseListPage pageToResponse(Page<Application> applicationPage) {
        if (applicationPage == null) return null;

        return new ApplicationResponseListPage(
                listToResponseList(applicationPage.getContent()),
                applicationPage.getNumber(),
                applicationPage.getTotalElements(),
                applicationPage.getTotalPages(),
                applicationPage.getSort().toString()
        );
    }
}
