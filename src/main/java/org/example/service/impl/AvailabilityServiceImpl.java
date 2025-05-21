package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.exception.HotelNotFoundException;
import org.example.model.entity.Hotel;
import org.example.repository.BookingRepository;
import org.example.repository.HotelRepository;
import org.example.service.AvailabilityService;
import org.example.util.ValidationUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    @Override
    public int checkAvailability(String hotelId, LocalDate date, String roomType) {
        ValidationUtils.validateHotelId(hotelId);
        ValidationUtils.validateDate(date);
        ValidationUtils.validateRoomType(roomType);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new HotelNotFoundException(hotelId));
            
        long totalRooms = hotel.getRooms().stream()
            .filter(r -> r.getRoomType().equals(roomType))
            .count();
            
        long bookedRooms = bookingRepository.findByHotelAndDateRange(
            hotelId, date, date.plusDays(1), roomType)
            .size();
            
        return (int) (totalRooms - bookedRooms);
    }

    @Override
    public int checkAvailability(String hotelId, LocalDate startDate, LocalDate endDate, String roomType) {
        ValidationUtils.validateHotelId(hotelId);
        ValidationUtils.validateDateRange(startDate, endDate);
        ValidationUtils.validateRoomType(roomType);
        return IntStream.range(0, (int) ChronoUnit.DAYS.between(startDate, endDate))
            .mapToObj(i -> checkAvailability(hotelId, startDate.plusDays(i), roomType))
            .min(Integer::compare)
            .orElse(0);
    }

    @Override
    public Map<String, Integer> searchAvailability(String hotelId, String roomType, int daysAhead) {
        ValidationUtils.validateDaysAhead(daysAhead);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);
        
        Map<String, Integer> availabilityMap = new LinkedHashMap<>();
        
        LocalDate current = today;
        while (current.isBefore(endDate)) {
            LocalDate next = current.plusDays(1);
            int available = checkAvailability(hotelId, current, next, roomType);
            if (available > 0) {
                String range = formatDateRange(current, next);
                availabilityMap.put(range, available);
            }
            current = next;
        }
        
        return availabilityMap;
    }
    
    private String formatDateRange(LocalDate start, LocalDate end) {
        return start.format(DateTimeFormatter.BASIC_ISO_DATE) + "-" +
               end.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

}