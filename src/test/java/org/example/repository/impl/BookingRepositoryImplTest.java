package org.example.repository.impl;

import org.example.exception.DataLoadingException;
import org.example.exception.RepositoryException;
import org.example.model.entity.Booking;
import org.example.repository.DataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRepositoryImplTest {

    @Mock
    private DataProvider<Booking> bookingDataProvider;

    @InjectMocks
    private BookingRepositoryImpl bookingRepository;

    private List<Booking> testBookings;

    @BeforeEach
    void setUp() {
        testBookings = List.of(
                new Booking("H1", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 3), "SGL", "Standard"),
                new Booking("H1", LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 7), "DBL", "Premium"),
                new Booking("H2", LocalDate.of(2025, 6, 2), LocalDate.of(2025, 6, 4), "SGL", "Standard")
        );
    }

    @Test
    void findByHotelAndDateRange_shouldReturnFilteredBookings() throws DataLoadingException {
        when(bookingDataProvider.getAll()).thenReturn(testBookings);

        String hotelId = "H1";
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 6);
        String roomType = "SGL";

        List<Booking> result = bookingRepository.findByHotelAndDateRange(
                hotelId, startDate, endDate, roomType);

        assertEquals(1, result.size());
        assertEquals("H1", result.get(0).getHotelId());
        assertEquals("SGL", result.get(0).getRoomType());
        verify(bookingDataProvider, times(1)).getAll();
    }

    @Test
    void findByHotelAndDateRange_shouldReturnEmptyListWhenNoMatches() throws DataLoadingException {
        when(bookingDataProvider.getAll()).thenReturn(testBookings);

        List<Booking> result = bookingRepository.findByHotelAndDateRange(
                "H3", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 10), "SUITE");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByHotelAndDateRange_shouldThrowRepositoryExceptionOnDataLoadingError() throws DataLoadingException {
        when(bookingDataProvider.getAll()).thenThrow(new DataLoadingException("Test error"));

        assertThrows(RepositoryException.class, () ->
                bookingRepository.findByHotelAndDateRange("H1", LocalDate.now(), LocalDate.now().plusDays(1), "SGL")
        );
    }

    @Test
    void findAll_shouldReturnAllBookings() throws DataLoadingException {
        when(bookingDataProvider.getAll()).thenReturn(testBookings);

        List<Booking> result = bookingRepository.findAll();

        assertEquals(3, result.size());
        verify(bookingDataProvider, times(1)).getAll();
    }

    @Test
    void findAll_shouldThrowRepositoryExceptionOnDataLoadingError() throws DataLoadingException {
        when(bookingDataProvider.getAll()).thenThrow(new DataLoadingException("Test error"));

        assertThrows(RepositoryException.class, () -> bookingRepository.findAll());
    }
}