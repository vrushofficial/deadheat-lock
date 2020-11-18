package com.vrush.microservices.booking.repositories;

import java.util.UUID;

import com.vrush.microservices.booking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingRepository extends JpaRepository<Booking, UUID> {

}
