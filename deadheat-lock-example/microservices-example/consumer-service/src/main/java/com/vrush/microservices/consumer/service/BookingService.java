package com.vrush.microservices.consumer.service;

import com.vrush.microservices.consumer.clients.BookingClient;
import com.vrush.microservices.consumer.dtos.BookingPostDTO;
import com.vrush.microservices.consumer.utils.Template;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingClient bookingClient;

    public String save(BookingPostDTO bookingPostDTO){
        bookingClient.saveBooking(bookingPostDTO);
        return Template.REDIRECT_BOOKING;
    }

}
