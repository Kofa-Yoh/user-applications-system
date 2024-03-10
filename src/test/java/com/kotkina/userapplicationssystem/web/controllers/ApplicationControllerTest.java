package com.kotkina.userapplicationssystem.web.controllers;

import com.kotkina.userapplicationssystem.utils.ApplicationTestUtils;
import com.kotkina.userapplicationssystem.utils.UserTestUtils;
import com.kotkina.userapplicationssystem.entities.Application;
import com.kotkina.userapplicationssystem.entities.ApplicationStatus;
import com.kotkina.userapplicationssystem.entities.RoleType;
import com.kotkina.userapplicationssystem.entities.User;
import com.kotkina.userapplicationssystem.securily.SecurityService;
import com.kotkina.userapplicationssystem.securily.UserDetailsImpl;
import com.kotkina.userapplicationssystem.services.ApplicationService;
import com.kotkina.userapplicationssystem.utils.ApplicationMapper;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponse;
import com.kotkina.userapplicationssystem.web.models.response.ApplicationResponseListPage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.refEq;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private ApplicationMapper applicationMapper;

    private final static String NEW_APPLICATION_REQUEST = "{\"text\":\"New Application\", \"status\":\"DRAFT\"}";

    private final static String CHANGE_APPLICATION_REQUEST = "{\"text\":\"Changed text\"}";

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createApplication_WithUserRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER});

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Application newApplication = ApplicationTestUtils.createApplication(1L, ApplicationStatus.DRAFT);

        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(newApplication);

        Mockito.when(securityService.getCurrentUser()).thenReturn(userDetails);
        Mockito.when(applicationService.createApplication(user, newApplication.getStatus(), newApplication.getText())).thenReturn(newApplication);
        Mockito.when(applicationMapper.applicationToResponse(newApplication)).thenReturn(applicationResponse);

        mockMvc.perform(post("/api/applications/new")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(NEW_APPLICATION_REQUEST)
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "operator", roles = {"OPERATOR"})
    void getApplicationByIdWithAvailableStatus_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(2L, "operator", "Operator", new RoleType[]{RoleType.ROLE_OPERATOR});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.SENT);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(application);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationMapper.applicationToResponse(application)).thenReturn(applicationResponse);

        mockMvc.perform(get("/api/applications/" + application.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "operator", roles = {"OPERATOR"})
    void getApplicationByIdWithUnAvailableStatus_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(2L, "operator", "Operator", new RoleType[]{RoleType.ROLE_OPERATOR});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.ACCEPTED);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(application);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationMapper.applicationToResponse(application)).thenReturn(applicationResponse);

        mockMvc.perform(get("/api/applications/" + application.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void changeApplicationBodyByIdWithAvailableStatus_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.DRAFT);
        Application changedApplication = ApplicationTestUtils.createApplication(1L, ApplicationStatus.DRAFT);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(changedApplication);
        changedApplication.setText("Changed text");

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.updateApplication(application)).thenReturn(changedApplication);
        Mockito.when(applicationMapper.applicationToResponse(refEq(changedApplication))).thenReturn(applicationResponse);

        mockMvc.perform(put("/api/applications/" + application.getId())
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(CHANGE_APPLICATION_REQUEST))
                .andExpect(status().isOk());

        Mockito.verify(securityService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationById(application.getId());
        Mockito.verify(applicationService, Mockito.times(1)).updateApplication(application);
        Mockito.verify(applicationMapper, Mockito.times(1)).applicationToResponse(refEq(changedApplication));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void changeApplicationBodyByIdWithUnavailableStatus_thenReturn401() throws Exception {
        User user = UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.SENT);
        Application changedApplication = ApplicationTestUtils.createApplication(1L, ApplicationStatus.SENT);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(changedApplication);
        changedApplication.setText("Changed text");

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.updateApplication(application)).thenReturn(changedApplication);
        Mockito.when(applicationMapper.applicationToResponse(refEq(changedApplication))).thenReturn(applicationResponse);

        mockMvc.perform(put("/api/applications/" + application.getId())
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(CHANGE_APPLICATION_REQUEST))
                .andExpect(status().isNotFound());

        Mockito.verify(securityService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationById(application.getId());
        Mockito.verify(applicationService, Mockito.times(0)).updateApplication(application);
        Mockito.verify(applicationMapper, Mockito.times(0)).applicationToResponse(refEq(changedApplication));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void changeApplicationStatusFromDraftToSentStatus_WithUserRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.DRAFT);
        Application changedApplication = ApplicationTestUtils.createApplication(1L, ApplicationStatus.SENT);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(changedApplication);
        changedApplication.setStatus(ApplicationStatus.SENT);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.updateApplication(application)).thenReturn(changedApplication);
        Mockito.when(applicationMapper.applicationToResponse(refEq(changedApplication))).thenReturn(applicationResponse);

        mockMvc.perform(put("/api/applications/" + application.getId() + "/status")
                        .param("change", "SENT"))
                .andExpect(status().isOk());

        Mockito.verify(securityService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationById(application.getId());
        Mockito.verify(applicationService, Mockito.times(1)).updateApplication(application);
        Mockito.verify(applicationMapper, Mockito.times(1)).applicationToResponse(refEq(changedApplication));
    }

    @Test
    @WithMockUser(username = "operator", roles = {"OPERATOR"})
    void changeApplicationStatusFromDraftToSentStatus_WithOperatorRole_thenReturn404() throws Exception {
        User user = UserTestUtils.createUser(2L, "operator", "Operator", new RoleType[]{RoleType.ROLE_OPERATOR});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.DRAFT);
        Application changedApplication = ApplicationTestUtils.createApplication(1L, ApplicationStatus.SENT);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(changedApplication);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.updateApplication(application)).thenReturn(changedApplication);
        Mockito.when(applicationMapper.applicationToResponse(application)).thenReturn(applicationResponse);

        mockMvc.perform(put("/api/applications/" + application.getId() + "/status")
                        .param("change", "SENT"))
                .andExpect(status().isNotFound());

        Mockito.verify(securityService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationById(application.getId());
        Mockito.verify(applicationService, Mockito.times(0)).updateApplication(application);
        Mockito.verify(applicationMapper, Mockito.times(0)).applicationToResponse(refEq(changedApplication));
    }

    @Test
    @WithMockUser(username = "operator", roles = {"OPERATOR"})
    void changeApplicationStatusFromSentToAcceptedStatus_WithOperatorRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(2L, "operator", "Operator", new RoleType[]{RoleType.ROLE_OPERATOR});
        Application application = ApplicationTestUtils.createApplication(1L, ApplicationStatus.SENT);
        Application changedApplication = ApplicationTestUtils.createApplication(1L, ApplicationStatus.ACCEPTED);
        ApplicationResponse applicationResponse = ApplicationTestUtils.applicationToResponse(changedApplication);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.updateApplication(application)).thenReturn(changedApplication);
        Mockito.when(applicationMapper.applicationToResponse(application)).thenReturn(applicationResponse);

        mockMvc.perform(put("/api/applications/" + application.getId() + "/status")
                        .param("change", "ACCEPTED"))
                .andExpect(status().isOk());

        Mockito.verify(securityService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationById(application.getId());
        Mockito.verify(applicationService, Mockito.times(1)).updateApplication(application);
        Mockito.verify(applicationMapper, Mockito.times(1)).applicationToResponse(refEq(changedApplication));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getApplicationsByAuthor_WithUserRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER});

        int page = 0;
        String sort = "ASC";

        String order = sort.toUpperCase();
        Sort sortOrder = order.equals("ASC") || order.equals("DESC")
                ? Sort.by(Sort.Direction.valueOf(order), "createdOn")
                : Sort.unsorted();
        Pageable nextPage = PageRequest.of(page, Integer.parseInt("5"), sortOrder);

        Page<Application> applicationsPage = ApplicationTestUtils.createApplicationsPageWithStatus(ApplicationStatus.DRAFT, nextPage);
        ApplicationResponseListPage response = ApplicationTestUtils.pageToResponse(applicationsPage);

        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.getApplicationsByUser(user, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationMapper.pageToResponse(applicationsPage)).thenReturn(response);

        mockMvc.perform(get("/api/applications/my"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getApplicationsByDraftStatusAndUsername_WithUserRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(1L, "user", "User", new RoleType[]{RoleType.ROLE_USER});

        String statusParam = "DRAFT";
        String usernameParam = "user";
        int pageParam = 0;
        String sortParam = "";

        ApplicationStatus applicationStatus = ApplicationStatus.get(statusParam);

        String order = sortParam.toUpperCase();
        Sort sortOrder = order.equals("ASC") || order.equals("DESC")
                ? Sort.by(Sort.Direction.valueOf(order), "createdOn")
                : Sort.unsorted();
        Pageable nextPage = PageRequest.of(pageParam, Integer.parseInt("5"), sortOrder);

        Page<Application> applicationsPage = ApplicationTestUtils.createApplicationsPageWithStatus(ApplicationStatus.DRAFT, nextPage);
        ApplicationResponseListPage response = ApplicationTestUtils.pageToResponse(applicationsPage);

        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.getApplicationsByStatus(applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserAndStatus(user, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationMapper.pageToResponse(applicationsPage)).thenReturn(response);

        mockMvc.perform(get("/api/applications/")
                        .param("status", statusParam)
                        .param("username", usernameParam)
                        .param("pageParam", Integer.toString(pageParam))
                        .param("sortParam", sortParam))
                .andExpect(status().isOk());

        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByStatus(applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationsByUserAndStatus(user, applicationStatus, nextPage);
        Mockito.verify(applicationMapper, Mockito.times(1)).pageToResponse(applicationsPage);
    }

    @Test
    @WithMockUser(username = "operator", roles = {"OPERATOR"})
    void getApplicationsBySentStatusWithoutUsername_WithOperatorRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(2L, "operator", "Operator", new RoleType[]{RoleType.ROLE_OPERATOR});

        String statusParam = "SENT";
        String usernameParam = null;
        int pageParam = 0;
        String sortParam = "";

        ApplicationStatus applicationStatus = ApplicationStatus.get(statusParam);

        String order = sortParam.toUpperCase();
        Sort sortOrder = order.equals("ASC") || order.equals("DESC")
                ? Sort.by(Sort.Direction.valueOf(order), "createdOn")
                : Sort.unsorted();
        Pageable nextPage = PageRequest.of(pageParam, Integer.parseInt("5"), sortOrder);

        Page<Application> applicationsPage = ApplicationTestUtils.createApplicationsPageWithStatus(ApplicationStatus.SENT, nextPage);
        ApplicationResponseListPage response = ApplicationTestUtils.pageToResponse(applicationsPage);

        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.getApplicationsByStatus(applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserAndStatus(user, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationMapper.pageToResponse(applicationsPage)).thenReturn(response);

        mockMvc.perform(get("/api/applications/")
                        .param("status", statusParam))
                .andExpect(status().isOk());

        Mockito.verify(applicationService, Mockito.times(1)).getApplicationsByStatus(applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByUserAndStatus(user, applicationStatus, nextPage);
        Mockito.verify(applicationMapper, Mockito.times(1)).pageToResponse(applicationsPage);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getApplicationsByAcceptedStatusAndUsername_WithAdminRole_thenReturnOk() throws Exception {
        User user = UserTestUtils.createUser(3L, "admin", "Admin", new RoleType[]{RoleType.ROLE_ADMIN});

        String statusParam = "ACCEPTED";
        String usernameParam = "user";
        int pageParam = 0;
        String sortParam = "";

        ApplicationStatus applicationStatus = ApplicationStatus.get(statusParam);

        String order = sortParam.toUpperCase();
        Sort sortOrder = order.equals("ASC") || order.equals("DESC")
                ? Sort.by(Sort.Direction.valueOf(order), "createdOn")
                : Sort.unsorted();
        Pageable nextPage = PageRequest.of(pageParam, Integer.parseInt("5"), sortOrder);

        Page<Application> applicationsPage = ApplicationTestUtils.createApplicationsPageWithStatus(ApplicationStatus.ACCEPTED, nextPage);
        ApplicationResponseListPage response = ApplicationTestUtils.pageToResponse(applicationsPage);

        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.getApplicationsByStatus(applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserAndStatus(user, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationMapper.pageToResponse(applicationsPage)).thenReturn(response);

        mockMvc.perform(get("/api/applications/")
                        .param("status", statusParam)
                        .param("username", usernameParam)
                        .param("pageParam", Integer.toString(pageParam))
                        .param("sortParam", sortParam))
                .andExpect(status().isOk());

        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByStatus(applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(1)).getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByUserAndStatus(user, applicationStatus, nextPage);
        Mockito.verify(applicationMapper, Mockito.times(1)).pageToResponse(applicationsPage);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getApplicationsByUnavailableStatusAndUsername_thenReturn401() throws Exception {
        User user = UserTestUtils.createUser(3L, "admin", "Admin", new RoleType[]{RoleType.ROLE_ADMIN});

        String statusParam = "DRAFT";
        String usernameParam = "user";
        int pageParam = 0;
        String sortParam = "";

        ApplicationStatus applicationStatus = ApplicationStatus.get(statusParam);

        String order = sortParam.toUpperCase();
        Sort sortOrder = order.equals("ASC") || order.equals("DESC")
                ? Sort.by(Sort.Direction.valueOf(order), "createdOn")
                : Sort.unsorted();
        Pageable nextPage = PageRequest.of(pageParam, Integer.parseInt("5"), sortOrder);

        Page<Application> applicationsPage = ApplicationTestUtils.createApplicationsPageWithStatus(ApplicationStatus.ACCEPTED, nextPage);
        ApplicationResponseListPage response = ApplicationTestUtils.pageToResponse(applicationsPage);

        Mockito.when(securityService.getCurrentUser()).thenReturn(new UserDetailsImpl(user));
        Mockito.when(applicationService.getApplicationsByStatus(applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationService.getApplicationsByUserAndStatus(user, applicationStatus, nextPage)).thenReturn(applicationsPage);
        Mockito.when(applicationMapper.pageToResponse(applicationsPage)).thenReturn(response);

        mockMvc.perform(get("/api/applications/")
                        .param("status", statusParam)
                        .param("username", usernameParam)
                        .param("pageParam", Integer.toString(pageParam))
                        .param("sortParam", sortParam))
                .andExpect(status().isUnauthorized());

        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByStatus(applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByUserNameContainingAndStatus(usernameParam, applicationStatus, nextPage);
        Mockito.verify(applicationService, Mockito.times(0)).getApplicationsByUserAndStatus(user, applicationStatus, nextPage);
        Mockito.verify(applicationMapper, Mockito.times(0)).pageToResponse(applicationsPage);
    }
}