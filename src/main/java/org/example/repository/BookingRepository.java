package org.example.repository;

import org.example.model.entity.Booking;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository {
    List<Booking> findByHotelAndDateRange(String hotelId, 
                                        LocalDate startDate, 
                                        LocalDate endDate, 
                                        String roomType);
    void save(Booking booking);
    List<Booking> findAll();
}