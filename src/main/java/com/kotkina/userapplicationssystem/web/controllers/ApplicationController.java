package com.kotkina.userapplicationssystem.web.controllers;

import com.kotkina.userapplicationssystem.entities.Application;
import com.kotkina.userapplicationssystem.entities.ApplicationStatus;
import com.kotkina.userapplicationssystem.entities.RoleType;
import com.kotkina.userapplicationssystem.entities.User;
import com.kotkina.userapplicationssystem.exceptions.CurrentUserNotDefinedException;
import com.kotkina.userapplicationssystem.exceptions.DataNotFoundException;
import com.kotkina.userapplicationssystem.exceptions.RequestNotAvailableException;
import com.kotkina.userapplicationssystem.securily.SecurityService;
import com.kotkina.userapplicationssystem.securily.UserDetailsImpl;
import com.kotkina.userapplicationssystem.services.ApplicationService;
import com.kotkina.userapplicationssystem.utils.ApplicationMapper;
import com.kotkina.userapplicationssystem.web.models.request.CreateApplicationRequest;
import com.kotkina.userapplicationssystem.web.models.request.UpdateApplicationRequest;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponse;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponseListPage;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    private final SecurityService securityService;

    private final ApplicationMapper applicationMapper;

    private final static String PAGE_DEFAULT_VALUE = "0";

    private final static String SIZE_DEFAULT_VALUE = "5";

    private final static String SORT_DEFAULT_VALUE = "";

    private final static List<ApplicationStatus> ADMIN_AVAILABLE_APPLICATION_STATUSES = List.of(ApplicationStatus.SENT, ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED);
    private final static List<ApplicationStatus> OPERATOR_AVAILABLE_APPLICATION_STATUSES = List.of(ApplicationStatus.SENT);
    private final static List<ApplicationStatus> USER_AVAILABLE_APPLICATION_STATUSES = List.of(ApplicationStatus.DRAFT, ApplicationStatus.SENT, ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED);

    @PostMapping("/new")
    @RolesAllowed("USER")
    public ResponseEntity<ApplicationResponse> createApplication(@RequestBody CreateApplicationRequest request) {
        if (request == null || request.getText() == null || request.getStatus() == null) {
            throw new IllegalArgumentException("Некорректное заполнение тела запроса.");
        }

        if (!request.getStatus().equals(ApplicationStatus.DRAFT) && !request.getStatus().equals(ApplicationStatus.SENT)) {
            throw new IllegalArgumentException("Статус заявки может быть только \"DRAFT\" и \"SENT\".");
        }

        return ResponseEntity.ok(applicationMapper.applicationToResponse(
                applicationService.createApplication(getCurrentUser(), request.getStatus(), request.getText())));
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "OPERATOR", "ADMIN"})
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);

        if (application != null) {
            User user = getCurrentUser();

            if (checkApplicationAvailableToRead(application, user)) {
                return ResponseEntity.ok(applicationMapper.applicationToResponse(application));
            }
        }

        throw new DataNotFoundException("Заявка с указанным id не найдена или у вас нет доступа к ней.");
    }

    @PutMapping("/{id}")
    @RolesAllowed("USER")
    public ResponseEntity<ApplicationResponse> changeApplicationBodyById(@PathVariable Long id,
                                                                         @RequestBody() UpdateApplicationRequest request) {
        if (request == null || request.getText() == null) {
            throw new IllegalArgumentException("Некорректное заполнение тела запроса.");
        }

        Application application = applicationService.getApplicationById(id);

        if (application != null) {
            User user = getCurrentUser();

            if (checkApplicationAvailableToUpdateBody(application, user)) {
                application.setText(request.getText());
                return ResponseEntity.ok(applicationMapper.applicationToResponse(
                        applicationService.updateApplication(application)));
            }
        }

        throw new DataNotFoundException("Заявка с указанным id не найдена или у вас нет доступа для ее изменения.");
    }

    @PutMapping("/{id}/status")
    @RolesAllowed({"USER", "OPERATOR"})
    public ResponseEntity<ApplicationResponse> changeApplicationStatusById(@PathVariable Long id,
                                                                           @RequestParam("change") String newStatus) {
        ApplicationStatus newApplicationStatus = getApplicationStatus(newStatus);

        Application application = applicationService.getApplicationById(id);

        if (application != null) {
            User user = getCurrentUser();

            if (application.getStatus().equals(newApplicationStatus) && checkApplicationAvailableToRead(application, user)) {
                throw new IllegalArgumentException("Статус заявки совпадает с указанным.");
            }

            if (checkApplicationAvailableToUpdateStatus(application, user, newApplicationStatus)) {
                application.setStatus(newApplicationStatus);
                return ResponseEntity.ok(applicationMapper.applicationToResponse(
                        applicationService.updateApplication(application)));
            }
        }

        throw new DataNotFoundException("Заявка с указанным id не найдена или у вас нет доступа для ее изменения.");
    }

    @GetMapping("/my")
    @RolesAllowed("USER")
    public ResponseEntity<ApplicationResponseListPage> getApplicationsByAuthor(@RequestParam(defaultValue = PAGE_DEFAULT_VALUE) int page,
                                                                               @RequestParam(defaultValue = SORT_DEFAULT_VALUE) String sort) {
        return ResponseEntity.ok(applicationMapper.pageToResponse(applicationService.getApplicationsByUser(
                getCurrentUser(), createPageableParam(page, sort))));
    }

    @GetMapping("/")
    @RolesAllowed({"USER", "OPERATOR", "ADMIN"})
    public ResponseEntity<ApplicationResponseListPage> getApplicationsByStatusAndUsername(@RequestParam String status,
                                                                                          @RequestParam(required = false) String username,
                                                                                          @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) int page,
                                                                                          @RequestParam(defaultValue = SORT_DEFAULT_VALUE) String sort) {
        User user = getCurrentUser();
        ApplicationStatus applicationStatus = getApplicationStatus(status);
        Pageable nextPage = createPageableParam(page, sort);

        if (checkApplicationStatusAvailableForUserRole(user, RoleType.ROLE_ADMIN, applicationStatus)
                || checkApplicationStatusAvailableForUserRole(user, RoleType.ROLE_OPERATOR, applicationStatus)) {
            if (username == null || username.isEmpty()) {
                return ResponseEntity.ok(applicationMapper.pageToResponse(
                        applicationService.getApplicationsByStatus(applicationStatus, nextPage)));
            }

            return ResponseEntity.ok(applicationMapper.pageToResponse(
                    applicationService.getApplicationsByUserNameContainingAndStatus(username, applicationStatus, nextPage)));
        }

        if (checkApplicationStatusAvailableForUserRole(user, RoleType.ROLE_USER, applicationStatus)) {
            if (username == null || user.getName().toLowerCase().contains(username.toLowerCase())) {
                return ResponseEntity.ok(applicationMapper.pageToResponse(
                        applicationService.getApplicationsByUserAndStatus(user, applicationStatus, nextPage)));
            }
        }

        throw new RequestNotAvailableException();
    }

    private User getCurrentUser() {
        UserDetailsImpl currentUser = securityService.getCurrentUser();
        if (currentUser == null) {
            throw new CurrentUserNotDefinedException();
        }

        return currentUser.getUser();
    }

    private ApplicationStatus getApplicationStatus(String text) {
        ApplicationStatus applicationStatus = ApplicationStatus.get(text);
        if (applicationStatus == null) {
            throw new IllegalArgumentException("Неверно указан статус заявки.");
        }
        return applicationStatus;
    }

    private Pageable createPageableParam(int page, String sort) {
        String order = sort.toUpperCase();
        Sort sortOrder = order.equals("ASC") || order.equals("DESC")
                ? Sort.by(Sort.Direction.valueOf(order), "createdOn")
                : Sort.unsorted();
        return PageRequest.of(page, Integer.parseInt(SIZE_DEFAULT_VALUE), sortOrder);
    }

    private boolean checkApplicationStatusAvailableForUserRole(User user, RoleType role, ApplicationStatus applicationStatus) {
        if (!user.hasRole(role)) return false;

        switch (role) {
            case ROLE_ADMIN -> {
                return ADMIN_AVAILABLE_APPLICATION_STATUSES.contains(applicationStatus);
            }
            case ROLE_OPERATOR -> {
                return OPERATOR_AVAILABLE_APPLICATION_STATUSES.contains(applicationStatus);
            }
            case ROLE_USER -> {
                return USER_AVAILABLE_APPLICATION_STATUSES.contains(applicationStatus);
            }
        }

        return false;
    }

    private boolean checkApplicationAvailableToRead(Application application, User user) {
        if (Objects.equals(application.getUser().getId(), user.getId())) {
            return true;
        }

        if (user.hasRole(RoleType.ROLE_OPERATOR) && OPERATOR_AVAILABLE_APPLICATION_STATUSES
                .contains(application.getStatus())) {
            return true;
        }

        return user.hasRole(RoleType.ROLE_ADMIN) && ADMIN_AVAILABLE_APPLICATION_STATUSES
                .contains(application.getStatus());
    }

    private boolean checkApplicationAvailableToUpdateBody(Application application, User user) {
        return Objects.equals(application.getUser().getId(), user.getId()) && application.getStatus().equals(ApplicationStatus.DRAFT);
    }

    private boolean checkApplicationAvailableToUpdateStatus(Application application, User user, ApplicationStatus newStatus) {
        return switch (application.getStatus()) {
            case DRAFT -> newStatus.equals(ApplicationStatus.SENT) && user.hasRole(RoleType.ROLE_USER);
            case SENT -> (newStatus.equals(ApplicationStatus.ACCEPTED)
                    || newStatus.equals(ApplicationStatus.REJECTED))
                    && user.hasRole(RoleType.ROLE_OPERATOR);
            default -> false;
        };
    }
}
