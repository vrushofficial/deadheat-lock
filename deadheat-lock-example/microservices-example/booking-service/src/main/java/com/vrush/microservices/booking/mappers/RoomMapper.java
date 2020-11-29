package com.vrush.microservices.booking.mappers;

import java.util.List;

import com.vrush.microservices.booking.dtos.RoomGetDTO;
import com.vrush.microservices.booking.dtos.RoomPostDTO;
import com.vrush.microservices.booking.entities.Room;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toEntity(RoomPostDTO dto);

    RoomGetDTO toDTO(Room entity);

    List<RoomGetDTO> toDTO(List<Room> entities);

    default Page<RoomGetDTO> toDTO(Page<Room> page) {
        List<RoomGetDTO> responses = toDTO(page.getContent());
        return new PageImpl<>(responses, page.getPageable(), page.getTotalElements());
    }
}
