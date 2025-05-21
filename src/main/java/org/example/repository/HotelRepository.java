package org.example.repository;

import org.example.model.entity.Hotel;

import java.util.List;
import java.util.Optional;

public interface HotelRepository {
    Optional<Hotel> findById(String hotelId);
    List<Hotel> findAll();
    void refresh();
}