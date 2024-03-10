package com.kotkina.userapplicationssystem.services;

import com.kotkina.userapplicationssystem.entities.Application;
import com.kotkina.userapplicationssystem.entities.ApplicationStatus;
import com.kotkina.userapplicationssystem.entities.User;
import com.kotkina.userapplicationssystem.repositories.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public Application getApplicationById(Long id) {
        return applicationRepository.findApplicationById(id)
                .orElse(null);
    }

    public Page<Application> getApplicationsByUser(User user, Pageable nextPage) {
        return applicationRepository.findApplicationsByUser(user, nextPage);
    }

    public Page<Application> getApplicationsByUserAndStatus(User user, ApplicationStatus status, Pageable nextPage) {
        return applicationRepository.findApplicationsByUserAndStatus(user, status, nextPage);
    }

    public Page<Application> getApplicationsByStatus(ApplicationStatus status, Pageable nextPage) {
        return applicationRepository.findApplicationsByStatus(status, nextPage);
    }

    public Page<Application> getApplicationsByUserNameContainingAndStatus(String name, ApplicationStatus status, Pageable nextPage) {
        return applicationRepository.findApplicationsByUser_NameContainingAndStatus(name, status, nextPage);
    }

    public Application createApplication(User user, ApplicationStatus status, String text) {
        return applicationRepository.save(new Application(user, status, text));
    }

    public Application updateApplication(Application application) {
        return applicationRepository.save(application);
    }
}
