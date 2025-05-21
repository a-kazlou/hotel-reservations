package org.example.exception;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(String hotelId) {
        super("Hotel not found with ID: " + hotelId);
    }
}