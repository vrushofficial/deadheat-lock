package com.vrush.microservices.searching.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.vrush.microservices.searching.entities.BookingDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BookingDatesRepository extends JpaRepository<BookingDates, UUID> {

    @Query("select dates from BookingDates dates inner join Booking booking on dates.booking.id = booking.id" +
            " where booking.room.id = :roomId")
    List<BookingDates> findBookingsByRoom(@Param("roomId") UUID roomId);

}
