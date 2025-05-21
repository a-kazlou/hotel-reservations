package org.example.repository.impl;

import org.example.exception.DataLoadingException;
import org.example.exception.RepositoryException;
import org.example.model.entity.Booking;
import org.example.repository.BookingRepository;
import org.example.repository.DataProvider;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private static final String FETCH_SPECIFIC_BOOKING_EXCEPTION = "Failed to fetch bookings by hotel and date range";
    private static final String FETCH_BOOKING_EXCEPTION = "Failed to fetch all bookings";
    private final DataProvider<Booking> bookingDataProvider;

    public BookingRepositoryImpl(DataProvider<Booking> bookingDataProvider) {
        this.bookingDataProvider = bookingDataProvider;
    }

    @Override
    public List<Booking> findByHotelAndDateRange(String hotelId,
                                                 LocalDate startDate,
                                                 LocalDate endDate,
                                                 String roomType) {
        try {
            return bookingDataProvider.getAll().stream()
                    .filter(booking -> booking.getHotelId().equals(hotelId))
                    .filter(booking -> booking.getRoomType().equals(roomType))
                    .filter(booking ->
                            isDateRangeOverlap(booking.getArrival(), booking.getDeparture(), startDate, endDate))
                    .collect(Collectors.toList());
        } catch (DataLoadingException e) {
            throw new RepositoryException(FETCH_SPECIFIC_BOOKING_EXCEPTION, e);
        }
    }

    @Override
    public void save(Booking booking) {
    }

    @Override
    public List<Booking> findAll() {
        try {
            return bookingDataProvider.getAll();
        } catch (DataLoadingException e) {
            throw new RepositoryException(FETCH_BOOKING_EXCEPTION, e);
        }
    }

    private boolean isDateRangeOverlap(LocalDate bookingStart,
                                       LocalDate bookingEnd,
                                       LocalDate queryStart,
                                       LocalDate queryEnd) {
        return bookingStart.isBefore(queryEnd) && bookingEnd.isAfter(queryStart);
    }
}