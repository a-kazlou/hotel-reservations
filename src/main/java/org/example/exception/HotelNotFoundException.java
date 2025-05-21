package org.example.exception;

public class HotelNotFoundException extends RuntimeException {
    private static final String HOTEL_NOT_FOUND_EXCEPTION = "Hotel not found with ID: ";

    public HotelNotFoundException(String hotelId) {
        super(HOTEL_NOT_FOUND_EXCEPTION + hotelId);
    }
}