package com.vrush.microservices.searching.mappers;

import java.util.List;

import com.vrush.microservices.searching.dtos.RoomGetDTO;
import com.vrush.microservices.searching.entities.Room;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;


@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomGetDTO toDTO(Room entity);

    List<RoomGetDTO> toDTO(List<Room> entities);

    default Page<RoomGetDTO> toDTO(Page<Room> page) {
        List<RoomGetDTO> responses = toDTO(page.getContent());
        return new PageImpl<>(responses, page.getPageable(), page.getTotalElements());
    }
}
