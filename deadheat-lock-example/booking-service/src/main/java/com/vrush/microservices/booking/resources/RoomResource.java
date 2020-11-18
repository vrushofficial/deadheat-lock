package com.vrush.microservices.booking.resources;

import com.vrush.microservices.booking.dtos.RoomPostDTO;
import com.vrush.microservices.booking.service.RoomService;
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
@RequestMapping("/rooms")
public class RoomResource {

    private final RoomService service;

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody RoomPostDTO roomDTO) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(service.save(roomDTO).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

}
