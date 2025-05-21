package org.example.util;

import java.time.LocalDate;
import java.util.Objects;

public final class ValidationUtils {
    private static final String HOTEL_ID_NULL_EMPTY = "Hotel ID cannot be null or empty";
    private static final String DATE_PAST = "Date cannot be in the past";
    private static final String DATE_RANGE_INVALID = "End date must be after start date";
    private static final String ROOM_TYPE_NULL_EMPTY = "Room type cannot be null or empty";
    private static final String DAYS_AHEAD_INVALID = "Days ahead must be positive";

    private ValidationUtils() {
    }

    public static String validateHotelId(String hotelId) {
        if (hotelId == null || hotelId.isEmpty()) {
            throw new IllegalArgumentException(HOTEL_ID_NULL_EMPTY);
        }
        return hotelId;
    }

    public static LocalDate validateDate(LocalDate date) {
        if (Objects.isNull(date) || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(DATE_PAST);
        }
        return date;
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        validateDate(startDate);
        validateDate(endDate);
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(DATE_RANGE_INVALID);
        }
    }

    public static String validateRoomType(String roomType) {
        if (roomType == null || roomType.isEmpty()) {
            throw new IllegalArgumentException(ROOM_TYPE_NULL_EMPTY);
        }
        return roomType;
    }

    public static int validateDaysAhead(int daysAhead) {
        if (daysAhead <= 0) {
            throw new IllegalArgumentException(DAYS_AHEAD_INVALID);
        }
        return daysAhead;
    }
}