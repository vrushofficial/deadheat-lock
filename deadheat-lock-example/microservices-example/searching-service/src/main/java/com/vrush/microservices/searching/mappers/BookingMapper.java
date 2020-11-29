package com.vrush.microservices.searching.mappers;

import java.util.List;

import com.vrush.microservices.searching.dtos.BookingGetDTO;
import com.vrush.microservices.searching.entities.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mappings({
            @Mapping(target = "idRoom", source = "room.id")
    })
    BookingGetDTO toDTO(Booking entity);

    List<BookingGetDTO> toDTO(List<Booking> entities);

    default Page<BookingGetDTO> toDTO(Page<Booking> page) {
        List<BookingGetDTO> responses = toDTO(page.getContent());
        return new PageImpl<>(responses, page.getPageable(), page.getTotalElements());
    }
}
