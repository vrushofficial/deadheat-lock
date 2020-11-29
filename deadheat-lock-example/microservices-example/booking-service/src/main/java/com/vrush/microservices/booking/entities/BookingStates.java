package com.vrush.microservices.booking.entities;

import com.vrush.microservices.booking.enums.BookingStateEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_states")
@Data
@NoArgsConstructor
public class BookingStates {

    public BookingStates(Booking booking, String state) {
        this.booking = booking;
        this.state = state;
    }

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booking")
    private Booking booking;

    @Column(name = "state")
    private String state;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        creationDate = LocalDateTime.now();
        lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }

    public BookingStateEnum getState() {
        return BookingStateEnum.valueOf(this.state);
    }
}
