package com.vrush.microservices.booking.repositories;

import java.util.UUID;

import com.vrush.microservices.booking.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

}
