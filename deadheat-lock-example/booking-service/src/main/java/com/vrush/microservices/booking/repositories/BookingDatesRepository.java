package com.vrush.microservices.booking.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.vrush.microservices.booking.entities.BookingDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BookingDatesRepository extends JpaRepository<BookingDates, UUID> {

    @Query("select dates from BookingDates dates inner join Booking booking on dates.booking.id = booking.id" +
            " where booking.room.id = :roomId and date in :dates")
    List<BookingDates> findBookingsByDatesAndRoom(@Param("roomId") UUID roomId, @Param("dates") List<LocalDate> dates);

}
