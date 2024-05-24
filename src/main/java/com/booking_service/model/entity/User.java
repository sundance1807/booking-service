package com.booking_service.model.entity;

import com.booking_service.model.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, length = 100)
    private String password;
    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(name = "telegram_link", length = 100)
    private String telegramLink;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Role role = Role.USER;

    @PrePersist
    public void prePersist() {
        if (this.registeredAt == null) {
            this.registeredAt = LocalDateTime.now();
        }
    }
}
