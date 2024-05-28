package com.booking_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Data
@ToString(exclude = "timeSlots")
@EqualsAndHashCode(exclude = "timeSlots")
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private LocalDate date;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
    @ManyToOne(cascade = CascadeType.ALL)
    private Room room;
    @ManyToMany
    @JoinTable(name = "booked_time_slots",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "time_slot_id"))
    private Set<TimeSlot> timeSlots;
}
