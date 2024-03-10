package com.kotkina.userapplicationssystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @SequenceGenerator(initialValue = 101, name = "application_seq", sequenceName = "application_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_seq")
    private Long id;

    @JoinColumn(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private ApplicationStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    @ToString.Exclude
    private User user;

    @Setter
    private String text;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    public Application(User user, ApplicationStatus status, String text) {
        this.user = user;
        this.status = status;
        this.text = text;
    }
}
