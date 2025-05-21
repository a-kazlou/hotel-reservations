package org.example.service;

import java.time.LocalDate;
import java.util.Map;

public interface AvailabilityService {
    int checkAvailability(String hotelId, LocalDate date, String roomType);
    int checkAvailability(String hotelId, LocalDate startDate, LocalDate endDate, String roomType);
    Map<String, Integer> searchAvailability(String hotelId, String roomType, int daysAhead);
}