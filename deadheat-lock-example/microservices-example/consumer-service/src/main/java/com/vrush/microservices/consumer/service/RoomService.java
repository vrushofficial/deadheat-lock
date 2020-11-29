package com.vrush.microservices.consumer.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vrush.microservices.consumer.annotations.TrackMethod;
import com.vrush.microservices.consumer.clients.SearchingClient;
import com.vrush.microservices.consumer.dtos.RoomGetDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final SearchingClient searchingClient;

    @TrackMethod
    public List<RoomGetDTO> findAll() {
        Page<RoomGetDTO> response = null;
        List<RoomGetDTO> rooms = new ArrayList<>();

        do {
            int page = response == null ? 0 : response.getPageable().getPageNumber() + 1;
            response = searchingClient.getRooms(page);
            rooms.addAll(response.getContent());
        } while (!response.isLast());

        return rooms;
    }

    @TrackMethod
    public List<RoomGetDTO> findAllAvailableRooms(@NonNull final LocalDate startDate,
                                                  @NonNull final LocalDate endDate,
                                                  final UUID roomId) {
        return searchingClient.getAvailableRooms(startDate, endDate, roomId);
    }

    @TrackMethod
    public RoomGetDTO findById(final UUID roomId) {
        return searchingClient.getRoomById(roomId);
    }

}
