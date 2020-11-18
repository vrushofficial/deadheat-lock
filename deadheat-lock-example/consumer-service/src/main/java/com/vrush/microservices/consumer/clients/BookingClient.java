package com.vrush.microservices.consumer.clients;

import com.vrush.microservices.consumer.config.FeignConfiguration;
import com.vrush.microservices.consumer.dtos.BookingPostDTO;
import com.vrush.microservices.consumer.dtos.RoomPostDTO;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "booking-service", configuration = FeignConfiguration.class,url = "http://localhost:8100")
@RibbonClient(name = "booking-service", configuration = FeignConfiguration.class)
public interface BookingClient {

    @PostMapping("/bookings")
    ResponseEntity<Void> saveBooking(@RequestBody BookingPostDTO bookingPostDTO);

    @PostMapping("/rooms")
    ResponseEntity<Void> saveRoom(@RequestBody RoomPostDTO roomPostDTO);

}
