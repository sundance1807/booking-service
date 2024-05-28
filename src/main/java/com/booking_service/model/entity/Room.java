package com.booking_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "name", unique = true, length = 155)
    private String name;
    private Integer floor;
    private Integer capacity;

}
