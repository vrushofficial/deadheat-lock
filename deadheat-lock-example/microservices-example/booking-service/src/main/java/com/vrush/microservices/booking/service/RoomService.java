package com.vrush.microservices.booking.service;

import com.vrush.microservices.booking.annotations.TrackMethod;
import com.vrush.microservices.booking.dtos.RoomGetDTO;
import com.vrush.microservices.booking.dtos.RoomPostDTO;
import com.vrush.microservices.booking.entities.Room;
import com.vrush.microservices.booking.mappers.RoomMapper;
import com.vrush.microservices.booking.repositories.RoomRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository repository;
    private final RoomMapper mapper;

    @TrackMethod
    @Transactional
    public RoomGetDTO save(@NonNull final RoomPostDTO dto) {
        return mapper.toDTO(repository.save(mapper.toEntity(dto)));
    }
    @TrackMethod
    @Transactional(readOnly = true)
    public Room findById(@NonNull UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Room with id " + id + " does not exists."));
    }

}
