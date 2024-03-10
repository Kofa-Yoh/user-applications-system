package com.kotkina.userapplicationssystem.repositories;

import com.kotkina.userapplicationssystem.entities.Application;
import com.kotkina.userapplicationssystem.entities.ApplicationStatus;
import com.kotkina.userapplicationssystem.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findApplicationById(Long id);

    Boolean existsApplicationById(Long id);

    Page<Application> findApplicationsByUser(User user, Pageable nextPage);

    Page<Application> findApplicationsByUserAndStatus(User user, ApplicationStatus status, Pageable nextPage);

    Page<Application> findApplicationsByStatus(ApplicationStatus status, Pageable nextPage);

    Page<Application> findApplicationsByUser_NameContainingAndStatus(String name, ApplicationStatus status, Pageable nextPage);
}
