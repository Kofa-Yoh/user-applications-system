package com.kotkina.userapplicationssystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @SequenceGenerator(initialValue = 8, name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    @JsonIgnore
    private String username;

    @Setter
    private String name;

    @Column(nullable = false)
    @JsonIgnore
    @Setter
    private String password;

    @Setter
    private Integer countryCode;

    @Setter
    private Integer cityCode;

    @Setter
    private String phoneNumber;

    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private Set<RoleType> roles = new HashSet<>();

    public User(String name, String password, Integer countryCode, Integer cityCode, String phoneNumber, Set<RoleType> roles) {
        this.name = name;
        this.password = password;
        this.countryCode = countryCode;
        this.cityCode = cityCode;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    @PrePersist
    public void initializeUUID() {
        if (username == null) {
            username = UUID.randomUUID().toString();
        }
    }

    public boolean hasRole(RoleType role) {
        return roles.contains(role);
    }

    public String getPhone() {
        return MessageFormat.format("+{0} {1} {2}", countryCode, cityCode, phoneNumber);
    }
}
