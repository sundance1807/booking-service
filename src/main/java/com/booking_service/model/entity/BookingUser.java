package com.booking_service.model.entity;

import com.booking_service.model.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class BookingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", unique = true, nullable = false, length = 155)
    private String username;
    @Column(name = "password", nullable = false, length = 155)
    private String password;
    @Column(name = "firstName", length = 155)
    private String firstName;
    @Column(name = "secondName", length = 155)
    private String secondName;
    @Column(name = "telegramLink", unique = true, length = 155)
    private String telegramLink;
    @Column(name = "registeredAt")
    private LocalDateTime registeredAt;
    @Column(nullable = false)
    private Role role = Role.USER;

    @PrePersist
    public void prePersist() {
        if (this.registeredAt == null) {
            this.registeredAt = LocalDateTime.now();
        }
    }
}
