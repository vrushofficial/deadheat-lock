package com.vrush.microservices.booking.repositories;

import java.util.UUID;

import com.vrush.microservices.booking.entities.BookingStates;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingStatesRepository extends JpaRepository<BookingStates, UUID> {

}
