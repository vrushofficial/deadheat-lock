package com.vrush.microservices.searching.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vrush.microservices.searching.annotations.TrackMethod;
import com.vrush.microservices.searching.dtos.BookingGetDTO;
import com.vrush.microservices.searching.entities.Booking;
import com.vrush.microservices.searching.mappers.BookingMapper;
import com.vrush.microservices.searching.repositories.BookingRepository;
import com.vrush.microservices.searching.specifications.builder.SpecificationBuilder;
import com.vrush.microservices.searching.specifications.domain.FieldIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    @TrackMethod
    @Transactional(readOnly = true)
    public Page<BookingGetDTO> search(Pageable pageable,
                                      UUID idRoom,
                                      String guestEmail,
                                      String state) {
        Specification<Booking> specification = SpecificationBuilder.init()
                .withEqualInSubclassFilter(new FieldIn("room", "id"), idRoom)
                .withEqualInListFieldFilter(new FieldIn("states", "state"), state)
                .withEqualFilter("guestEmail", guestEmail)
                .buildSpec();
        return mapper.toDTO(repository.findAll(specification, pageable));
    }

}
