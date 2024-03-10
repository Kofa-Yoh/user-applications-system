package com.kotkina.userapplicationssystem.utils;

import com.kotkina.userapplicationssystem.entities.Application;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponse;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponseListPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UserMapper userMapper;

    public ApplicationResponse applicationToResponse(Application application) {
        if (application == null) return null;

        return ApplicationResponse.builder()
                .id(application.getId())
                .user(userMapper.userToApplicationUserResponse(application.getUser()))
                .status(application.getStatus().getText())
                .text(application.getText())
                .createdOn(application.getCreatedOn().format(FORMATTER))
                .updatedOn(application.getUpdatedOn().format(FORMATTER))
                .build();
    }

    public List<ApplicationResponse> listToResponseList(List<Application> applications) {
        return applications.stream()
                .map(this::applicationToResponse)
                .toList();
    }

    public ApplicationResponseListPage pageToResponse(Page<Application> applicationPage) {
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
