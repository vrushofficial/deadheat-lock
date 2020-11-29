package com.vrush.microservices.booking.resources;

import com.vrush.microservices.booking.dtos.BookingPostDTO;
import com.vrush.microservices.booking.entities.Booking;
import com.vrush.microservices.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingResource {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody BookingPostDTO bookingDTO) throws NoSuchMethodException {

        Booking saved = service.save(bookingDTO, bookingDTO.getIdRoom().toString());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId()).toUri();

        service.sendEventBookingCreated(saved, bookingDTO.getCreditCardNumberEncrypted(),
                bookingDTO.getExpireDateEncrypted(), bookingDTO.getCcvEncrypted());
        return ResponseEntity.created(uri).build();
    }

}
